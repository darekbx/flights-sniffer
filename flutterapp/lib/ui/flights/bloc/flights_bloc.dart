import 'package:bloc/bloc.dart';
import 'package:flutter/services.dart';
import 'package:flutterapp/repository/local/aircraft.dart';
import 'package:flutterapp/repository/local/aircrafticons.dart';
import 'package:flutterapp/repository/local/airports.dart';
import 'package:flutterapp/repository/remote/flights.dart';
import 'package:flutterapp/repository/remote/model/models.dart';
import 'package:flutterapp/ui/flights/bloc/flights_event.dart';
import 'package:flutterapp/ui/flights/bloc/flights_state.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:geo/geo.dart';

const String DEFAULT_IATA = "WAW";
const String DEFAULT_BOUNDS = "57.00,47.00,12.00,26.00";

const String DEPARTURES_ON = "departuresOnKey";
const String ARRIVALS_ON = "arrivalsOnKey";
const String AIRPORT_CODE = "airportCodeKey";
const String BOUNDS = "boundsKey";

class FlightsBloc extends Bloc<FlightsEvent, FlightsState> {
  final AssetBundle assetBundle;

  Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  final FlightsRepository _flightsRepository = FlightsRepository();
  late AirportRepository _airportRepository;
  late AircraftRepository _aircraftRepository;
  late AircraftIconsRepository _aircraftIconsRepository;

  FlightsBloc(FlightsState initialState, this.assetBundle)
      : super(initialState) {
    _airportRepository = AirportRepository(assetBundle);
    _aircraftRepository = AircraftRepository(assetBundle);
    _aircraftIconsRepository = AircraftIconsRepository(assetBundle);
  }

  @override
  Stream<FlightsState> mapEventToState(FlightsEvent event) async* {
    if (event is LoadFlights) {
      yield Loading();
      yield* _mapObserveFlightsToState();
    }
  }

  Stream<FlightsState> _mapObserveFlightsToState() async* {
    try {
      var airports = await _airportRepository.loadAirports();
      var aircraftDictionary = await _aircraftRepository
          .loadAircraftDictionary();
      var bigAircraft = await _aircraftRepository.loadBigAircraft();

      var bounds = await _selectedbounds();
      var flights = await _flightsRepository.loadFlights(bounds);
      var selectedIataCode = await _selectedIataCode();
      var selectedAirport = airports.firstWhere((airport) =>
      airport.iataCode == selectedIataCode);

      for (var flight in flights) {
        await _loadFlightIcon(flight);
        _setIfIsBigPlane(flight, bigAircraft);
        _loadAircraftName(aircraftDictionary, flight);
        _loadDistanceLeft(
            flight, LatLng(selectedAirport.lat, selectedAirport.lng));
      }

      var byDestination = await _filterByDestination();
      var byOrigin = await _filterByOrigin();
      flights = flights.where((flight) =>
      _filterArrived(flight) &&
          _fillterByDestination(
              flight, byDestination, byOrigin, selectedIataCode)).toList();

      flights.sort((a, b) => a.distanceLeft.compareTo(b.distanceLeft));

      yield FlightsLoaded(flights, selectedAirport.name);
    } catch (e) {
      print(e);
      yield Error("$e");
    }
  }

  bool _fillterByDestination(Flight flight, bool byDestination, bool byOrigin,
      String airportIata) {
    if (byDestination && byOrigin) {
      return flight.destination == airportIata || flight.origin == airportIata;
    } else if (byDestination) {
      return flight.destination == airportIata;
    } else if (byOrigin) {
      return flight.origin == airportIata;
    } else {
      return false;
    }
  }

  bool _filterArrived(Flight flight) {
    return flight.speed > 0 &&
        flight.altitude > 0;
  }

  void _loadDistanceLeft(Flight flight, LatLng selectedAirportLatLng) {
    var flightLatLng = LatLng(flight.lat, flight.lng);
    var distance = computeDistanceBetween(flightLatLng, selectedAirportLatLng);
    flight.distanceLeft = distance ~/ 1000;
  }

  void _setIfIsBigPlane(Flight flight, List<String> bigAircraft) {
    flight.isBigPlane = bigAircraft.contains(flight.icao);
  }

  void _loadAircraftName(Map<String, String> aircraftDictionary,
      Flight flight) {
    if (aircraftDictionary.containsKey(flight.icao)) {
      flight.aircraftName =
      "${aircraftDictionary[flight.icao]} (${flight.icao})";
    }
  }

  Future<void> _loadFlightIcon(Flight flight) async {
    try {
      var icon = await _aircraftIconsRepository.loadAircraftIcon(flight.icao);
      if (icon != null) {
        flight.icon = icon;
      }
    } catch (e) {
      print(e);
    }
  }

  Future<bool> _filterByDestination() async {
    return (await _prefs).getBool(DEPARTURES_ON) ?? true;
  }

  Future<bool> _filterByOrigin() async {
    return (await _prefs).getBool(ARRIVALS_ON) ?? false;
  }

  Future<String> _selectedIataCode() async {
    return (await _prefs).getString(AIRPORT_CODE) ?? DEFAULT_IATA;
  }

  Future<List<double>> _selectedbounds() async {
    var boundsString = (await _prefs).getString(BOUNDS) ?? DEFAULT_BOUNDS;
    return boundsString.split(",")
        .map((chunk) => double.tryParse(chunk) ?? 0.0)
        .toList();
  }
}
