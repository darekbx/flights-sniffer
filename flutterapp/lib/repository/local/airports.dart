import 'dart:convert';
import 'package:flutter/services.dart';

import 'model/airportmodel.dart';

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
