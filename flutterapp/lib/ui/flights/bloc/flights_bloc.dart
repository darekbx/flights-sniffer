import 'package:bloc/bloc.dart';
import 'package:flutter/services.dart';
import 'package:flutterapp/repository/local/aircraft.dart';
import 'package:flutterapp/repository/local/aircrafticons.dart';
import 'package:flutterapp/repository/local/airports.dart';
import 'package:flutterapp/repository/remote/flights.dart';
import 'package:flutterapp/repository/remote/model/models.dart';
import 'package:flutterapp/ui/flights/bloc/flights_event.dart';
import 'package:flutterapp/ui/flights/bloc/flights_state.dart';
import 'package:geo/geo.dart';

class FlightsBloc extends Bloc<FlightsEvent, FlightsState> {
  final AssetBundle assetBundle;

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

  /// TODO
  var _bounds = [57.00, 47.00, 12.00, 26.00];
  var _selectedAirportIata = "WAW";
  var _selectedAirportLatLng = LatLng(52.1656990051, 20.967100143399996);

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
      var aircraftDictionary = await _aircraftRepository.loadAircraftDictionary();
      var bigAircraft = await _aircraftRepository.loadBigAircraft();

      var flights = await _flightsRepository.loadFlights(_bounds);
      var selectedAirport = airports.firstWhere((airport) => airport.iataCode == _selectedAirportIata);

      flights.forEach((flight) async {
        await _loadFlightIcon(flight);
        _setIfIsBigPlane(flight, bigAircraft);
        _loadAircraftName(aircraftDictionary, flight);
        _loadDistanceLeft(flight);
      });

      flights = flights.where((flight) {
        // TODO filter by origin and destination
        return flight.destination == _selectedAirportIata;
      }).toList();

      yield FlightsLoaded(flights, selectedAirport.name);

    } catch (e) {
      print(e);
      yield Error("$e");
    }
  }

  void _loadDistanceLeft(Flight flight) {
    var flightLatLng = LatLng(flight.lat, flight.lng);
    var distance = computeDistanceBetween(flightLatLng, _selectedAirportLatLng);
    flight.distanceLeft = (distance / 1000).toInt();
  }

  void _filterByAirport() {
  }

  void _setIfIsBigPlane(Flight flight, List<String> bigAircraft) {
    flight.isBigPlane = bigAircraft.contains(flight.icao);
  }

  void _loadAircraftName(Map<String, String> aircraftDictionary, Flight flight) {
    if (aircraftDictionary.containsKey(flight.icao)) {
      flight.aircraftName = "${aircraftDictionary[flight.icao]} (${flight.icao})";
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
}
