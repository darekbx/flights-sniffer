import 'dart:typed_data';
import 'dart:ui' as UI;
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'model/iconholder.dart';

class AircraftIconsRepository {
  static const String AIRCRAFT_SPRITE = "assets/aircraft_sprite.png";
  static const String AIRCRAFT_SPRITE_FRAMES = "assets/aircraft_frames.json";
  static const int FRAME_ROTATION = 45;

  AssetBundle _assetBundle;

  AircraftIconsRepository(this._assetBundle);

  Future<Uint8List?> loadAircraftIcon(String name) async {
    var iconsInfoJson = await loadAircraftSpriteFramesJson();
    var spriteBytes = await loadAircraftSpriteBytes();
    var icons = _parseIconHolders(iconsInfoJson);
    var icon = icons.firstWhere((icon) => icon.names.contains(name));

    Uint8List list = Uint8List.view(spriteBytes.buffer);
    var codec = await UI.instantiateImageCodec(list);
    var nextFrame = await codec.getNextFrame();
    var image = nextFrame.image;

    var pictureRecorder = UI.PictureRecorder();
    Canvas canvas = Canvas(pictureRecorder);
    Rect srcRect = Rect.fromLTWH(
        0, 0, image.width.toDouble(), image.height.toDouble());
    Rect dstRect = Rect.fromLTWH(icon.x, icon.y, icon.width, icon.height);
    canvas.drawImageRect(image, srcRect, dstRect, Paint());

    var outImage = await pictureRecorder.endRecording().toImage(
        icon.width.toInt(), icon.height.toInt());
    return (await outImage.toByteData())?.buffer?.asUint8List();
  }

  List<IconHolder> _parseIconHolders(String jsonData) {
    var linkedList = json.decode(jsonData);
    var iconsList = linkedList["icons"];

    List<IconHolder> icons = [];

    for (var i = 0; i < iconsList.length; i++) {
      var key = iconsList.keys.toList()[i];
      var iconHolderObject = iconsList[key];
      List<String> names = [key];

      var aliases = iconHolderObject["aliases"]
          .map<String>((value) => "$value")
          .toList();
      names.addAll(aliases);

      var frames = iconHolderObject["frames"][0];
      var frame = frames["$FRAME_ROTATION"];

      var x = double.parse("${frame["x"]}");
      var y = double.parse("${frame["y"]}");
      var width = double.parse("${frame["w"]}");
      var height = double.parse("${frame["h"]}");

      var iconHolder = IconHolder(names, x, y, width, height);
      icons.add(iconHolder);
    }

    return icons;
  }

  Future<String> loadAircraftSpriteFramesJson() async {
    return await _assetBundle.loadString(AIRCRAFT_SPRITE_FRAMES);
  }

  Future<ByteData> loadAircraftSpriteBytes() async {
    return await _assetBundle.load(AIRCRAFT_SPRITE);
  }
}
