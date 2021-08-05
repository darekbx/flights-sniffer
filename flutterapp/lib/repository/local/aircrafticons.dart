import 'dart:async';
import 'dart:typed_data';
import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:collection/collection.dart';
import 'model/iconholder.dart';

class AircraftIconsRepository {
  static const String AIRCRAFT_SPRITE = "assets/aircraft_sprite.png";
  static const String AIRCRAFT_SPRITE_FRAMES = "assets/aircraft_frames.json";
  static const int FRAME_ROTATION = 45;

  AssetBundle _assetBundle;

  AircraftIconsRepository(this._assetBundle);

  Future<IconHolder?> loadAircraftIcon(String name) async {
    var iconsInfoJson = await loadAircraftSpriteFramesJson();
    var icons = _parseIconHolders(iconsInfoJson);
    return icons.firstWhereOrNull((icon) => icon.names.contains(name));
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

      var frames = iconHolderObject["frames"];
      var frame = frames[0]["45"];
      if (frame == null) {
        frame = frames[0]["0"];
      }
      if (frame != null) {
        var x = double.parse("${frame["x"]}");
        var y = double.parse("${frame["y"]}");
        var width = double.parse("${frame["w"]}");
        var height = double.parse("${frame["h"]}");

        var iconHolder = IconHolder(names, x, y, width, height);
        icons.add(iconHolder);
      }
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
