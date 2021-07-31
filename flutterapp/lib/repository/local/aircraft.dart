import 'dart:convert';
import 'package:flutter/services.dart';

class AircraftRepository {
  static const String BIG_AIRCRAFT = "big_aircraft.json";
  static const String AIRCRAFT_DICTIONARY = "aircraft_dictionary.json";

  AssetBundle _assetBundle;

  AircraftRepository(this._assetBundle);

  /// Returns ICAO codes of the defined big aircrafts
  Future<List<String>> loadBigAircraft() async {
    var data = await loadBigAircraftJson();
    var jsonObject = json.decode(data);
    return jsonObject.map<String>((json) => json.toString()).toList();
  }

  /// Returns ICAO codes with aircraft name
  Future<Map<String, String>> loadAircraftDictionary() async {
    var data = await loadAircraftDictionaryJson();
    var jsonObject = json.decode(data);
    return Map.fromIterable(jsonObject,
        key: (row) => row["icao"], value: (row) => row["model"]);
  }

  Future<String> loadBigAircraftJson() async {
    return await _assetBundle.loadString(BIG_AIRCRAFT);
  }

  Future<String> loadAircraftDictionaryJson() async {
    return await _assetBundle.loadString(AIRCRAFT_DICTIONARY);
  }
}
