import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutterapp/repository/remote/model/models.dart';
import 'package:flutterapp/repository/local/model/iconholder.dart';
import 'dart:ui' as ui;
import 'dart:math';

class ImagePainter extends CustomPainter {

  final ui.Image image;
  final IconHolder iconHolder;

  ImagePainter(this.image, this.iconHolder);

  @override
  void paint(Canvas canvas, Size size) {
    Rect srcRect = Rect.fromLTWH(
        0, 0, iconHolder.width, iconHolder.height);
    Rect dstRect = Rect.fromLTWH(
        iconHolder.x, iconHolder.y, iconHolder.width, iconHolder.height);
    canvas.drawImageRect(image, dstRect, srcRect, Paint());
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

class FlightWidget extends StatelessWidget {

  final Flight flight;
  final ui.Image planeSprites;

  FlightWidget(this.flight, this.planeSprites);

  @override
  Widget build(BuildContext context) {
    return
      Padding(
          padding: EdgeInsets.all(8),
          child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _provideIcon(),
                _planeInfo(),
                _distanceLeft(),
                _flightInfo()
              ])
      );
  }

  Widget _distanceLeft() {
    return Text("${flight.distanceLeft}km left",
        style: TextStyle(fontWeight: FontWeight.bold));
  }

  Widget _flightInfo() =>
    Container(
      width: 100,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Text("${flight.origin} to ${flight.destination}"),
          Text("${flight.flight}",
              style: TextStyle(
                  color: Colors.white60, fontWeight: FontWeight.bold),
              textScaleFactor: 0.95)
        ],
      ));

  Widget _planeInfo() =>
      SizedBox(
          width: 180,
          child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text("${flight.aircraftName} (${flight.icao})",
                    style: TextStyle(color: Colors.white),
                    overflow: TextOverflow.ellipsis),
                Container(height: 4),
                Row(
                  children: [
                    Icon(Icons.speed, size: 16, color: Colors.white60),
                    Text("${flight.speedInKmH()}km/h",
                        style: TextStyle(color: Colors.white60),
                        textScaleFactor: 0.85),
                    Container(width: 8),
                    Icon(Icons.terrain, size: 16, color: Colors.white60),
                    Text("${flight.altitudeInMeters()}m",
                        style: TextStyle(color: Colors.white60),
                        textScaleFactor: 0.85)
                  ],
                ),
              ]));

  Widget _provideIcon() {
    Widget _icon() {
      if (flight.icon == null) {
        return Icon(Icons.warning);
      }
      return SizedBox(child: CustomPaint(
          painter: ImagePainter(planeSprites, flight.icon!),
          child: Container()
      ), width: 29, height: 29
      );
    }
    return Stack(
      children: [
        _icon(),
        flight.isBigPlane ?
        Transform.rotate(
            child: Icon(Icons.double_arrow, size: 10), angle: -90 * pi / 180)
            : Container()
      ],
    );
  }
}
