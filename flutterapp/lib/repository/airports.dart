import 'package:flutter/cupertino.dart';
import 'dart:convert';

import 'package:flutter/services.dart';

class AirportModel {
  final String iataCode;
  final String countryCode;
  final String name;
  final double lat;
  final double lng;

  AirportModel({
    required this.iataCode,
    required this.countryCode,
    required this.name,
    required this.lat,
    required this.lng
  });

  factory AirportModel.fromJson(Map<String, dynamic> json) {
    var coordinates = json['coordinates'].toString().split(", ");
    return AirportModel(
        iataCode: json['iata_code'],
        countryCode: json['iso_country'],
        name: json['name'],
        lat: double.parse(coordinates[1]),
        lng: double.parse(coordinates[0])
    );
  }
}

class AirportRepository {

  static const String AIRPORT_CODES = "airport_codes.json";

  AssetBundle _assetBundle;

  AirportRepository(this._assetBundle);

  Future<List<AirportModel>> loadAirports() async {
    var data = await loadAirportsJson();
    var jsonObject = json.decode(data);
    return jsonObject.map<AirportModel>((json) =>
        AirportModel.fromJson(json)).toList();
  }

  Future<String> loadAirportsJson() async {
    return await _assetBundle.loadString(AIRPORT_CODES);
  }
}
