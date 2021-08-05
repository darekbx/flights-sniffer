import 'dart:typed_data';
import 'dart:ui';

import 'package:flutterapp/repository/local/model/iconholder.dart';

class Flight {
  final String flightId;
  final String destination;
  final String origin;
  final String icao;
  final String flight;
  final String callSign;
  final int timestamp;
  final int altitude;
  final int speed; // In knots
  final double lat;
  final double lng;
  final int rotation;

  Flight({
    required this.flightId,
    required this.destination,
    required this.origin,
    required this.icao,
    required this.flight,
    required this.callSign,
    required this.timestamp,
    required this.altitude,
    required this.speed,
    required this.lat,
    required this.lng,
    required this.rotation
  });

  IconHolder? icon;
  int distanceLeft = 0;
  String aircraftName = "";
  var isBigPlane = false;

  int speedInKmH() => (speed.toDouble() * 1.85200).toInt();

  int altitudeInMeters() => (altitude.toDouble() * 0.3048).toInt();

  int mapRotation() =>
      45 /* because icon is rotated by 45 degress by default */ - rotation;
}

class FlightDetails {
  final String estimatedText; // status.text
  final String statusColor; // status.icon
  final Aircraft aircraft;
  final String airline; // airline.name
  final Airport origin; // airport.origin
  final Airport destination; // airport.destination
  final List<Trail> trail;

  FlightDetails(this.estimatedText, this.statusColor, this.aircraft,
      this.airline, this.origin, this.destination, this.trail);
}

class Aircraft {
  final String model; // aircraft.model.text
  final String? imageUrl; // aircraft.images.medium[0].src

  Aircraft(this.model, this.imageUrl);
}

class Airport {
  final String iata; // airport.*.code.iata
  final String name; // airport.*.name

  Airport(this.iata, this.name);
}

class Trail {
  final double lat; // trail[x].lat
  final double lng; // trail[x].lng
  final int alt; // trail[x].alt
  final int spd; // trail[x].spd
  final int ts; // trail[x].ts

  Trail(this.lat, this.lng, this.alt, this.spd, this.ts);
}
