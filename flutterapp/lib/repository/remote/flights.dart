
import 'dart:convert';
import 'dart:io';

import 'package:flutterapp/repository/remote/model/models.dart';
import 'package:http/http.dart' as http;

class FlightsRepository {

  static const String FLIGHTRADAR24_BASE_URL = "https://data-live.flightradar24.com";

  http.Client client = http.Client();

  Future<List<Flight>> loadFlights(List<double> bounds) async {
    List<Flight> flights = [];
    try {
      var jsonData = await _loadFlightsJson(bounds);
      var data = json.decode(jsonData);

      for (var key in data.keys.toList()) {
        if (data[key] is List) {
          var row = data[key];
          var flight = Flight(
              flightId: key,
              destination: row[12],
              origin: row[11],
              icao: row[8],
              flight: row[13],
              callSign: row[16],
              timestamp: row[10],
              altitude: row[4],
              speed: row[5],
              lat: row[1],
              lng: row[2],
              rotation: row[3]
          );
          flights.add(flight);
        }
      }

    } catch (e) {
      print(e);
    }
    return flights;
  }

  Future<FlightDetails?> loadFlightDetails(String flightId) async {
    try {
      var jsonData = await _loadFlighDetailsJson(flightId);
      var data = json.decode(jsonData);

      Aircraft aircraft = _parseAircraft(data);
      Airport originAirport = _parseAirport(data, "origin");
      Airport desinationAirport = _parseAirport(data, "destination");
      List<Trail> trail = _parseTrail(data);
      var estimatedText = data["status"]["text"];
      var statusColor = data["status"]["icon"];
      var airline = data["airline"]["name"];

      return FlightDetails(
          estimatedText,
          statusColor,
          aircraft,
          airline,
          originAirport,
          desinationAirport,
          trail);
    } catch (e) {
      print(e);
    }

    return null;
  }

  Aircraft _parseAircraft(dynamic data) {
    var aircraftData = data["aircraft"];
    var aircraftModel = aircraftData["model"]["text"];
    var images = aircraftData["images"]["medium"];
    var image = images.length > 0 ? images[0]["src"] : null;
    return Aircraft(aircraftModel, image);
  }

  Airport _parseAirport(dynamic data, String type) {
    var iata = data["airport"][type]["code"]["iata"];
    var name = data["airport"][type]["name"];
    return Airport(iata, name);
  }

  List<Trail> _parseTrail(dynamic data) {
    return data["trail"]
        .map<Trail>((row) =>
        Trail(row["lat"], row["lng"], row["alt"], row["spd"], row["ts"]))
        .toList();
  }

  Future<String> _loadFlightsJson(List<double> bounds) async {
    var boundsString = bounds.join(",");
    var url = "$FLIGHTRADAR24_BASE_URL/zones/fcgi/feed.js?bounds=$boundsString";
    var response = await client.get(Uri.parse(url));
    if (response.statusCode == 200) {
      return response.body;
    } else {
      throw HttpException("HTTP ${response.statusCode}");
    }
  }

  Future<String> _loadFlighDetailsJson(String flightId) async {
    var url = "$FLIGHTRADAR24_BASE_URL/clickhandler/?flight=$flightId";
    var response = await client.get(Uri.parse(url));
    if (response.statusCode == 200) {
      return response.body;
    } else {
      throw HttpException("HTTP ${response.statusCode}");
    }
  }
}
