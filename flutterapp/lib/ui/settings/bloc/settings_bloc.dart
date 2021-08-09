import 'package:bloc/bloc.dart';
import 'package:flutter/services.dart';
import 'package:flutterapp/repository/local/airports.dart';
import 'package:flutterapp/repository/local/settingsmodel.dart';
import 'package:flutterapp/ui/flights/bloc/flights_bloc.dart';
import 'package:flutterapp/ui/settings/bloc/settings_event.dart';
import 'package:flutterapp/ui/settings/bloc/settings_state.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SettingsBloc extends Bloc<SettingsEvent, SettingsState> {

  final AssetBundle assetBundle;

  Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  late AirportRepository _airportRepository;

  SettingsBloc(SettingsState initialState, this.assetBundle)
      : super(initialState) {
    _airportRepository = AirportRepository(assetBundle);
  }

  @override
  Stream<SettingsState> mapEventToState(SettingsEvent event) async* {
    if (event is LoadSettings) {
      yield Loading();
      yield* _mapLoadSettingsState();
    } else if (event is SaveDeparturesEnabled) {
      _saveBoolean(DEPARTURES_ON, event.isEnabled);
    } else if (event is SaveArrivalsEnabled) {
      _saveBoolean(ARRIVALS_ON, event.isEnabled);
    } else if (event is SaveIataCode) {
      _saveString(AIRPORT_CODE, event.iataCode);
    } else if (event is SaveBounds) {
      _saveString(BOUNDS, event.bounds);
    }
  }

  Stream<SettingsState> _mapLoadSettingsState() async* {
    try {
      var iataCodes = (await _airportRepository.loadAirports())
          .map((airport) => airport.iataCode).toList();

      var departuresEnabled = (await _prefs).getBool(DEPARTURES_ON) ?? true;
      var arrivalsEnabled = (await _prefs).getBool(ARRIVALS_ON) ?? true;
      var iataCode = (await _prefs).getString(AIRPORT_CODE) ?? DEFAULT_IATA;
      var bounds = (await _prefs).getString(BOUNDS) ?? DEFAULT_BOUNDS;

      yield SettingsLoaded(SettingsModel(
          iataCodes, departuresEnabled, arrivalsEnabled, iataCode, bounds)
      );
    } catch (e) {
      yield Error("$e");
    }
  }

  void _saveBoolean(String key, bool value) async {
    await (await _prefs).setBool(key, value);
  }
  void _saveString(String key, String value) async {
    await (await _prefs).setString(key, value);
  }
}
