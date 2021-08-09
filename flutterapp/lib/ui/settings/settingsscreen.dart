import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutterapp/repository/local/settingsmodel.dart';
import 'package:flutterapp/ui/flights/bloc/flights_bloc.dart';
import 'bloc/settings_bloc.dart';
import 'bloc/settings_event.dart';
import 'bloc/settings_state.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({ Key? key }) : super(key: key);

  @override
  _SettingsScreenState createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {

  late TextEditingController _boundsController;
  late SettingsBloc _settingsBloc;

  late List<String> _airports;
  bool _trackDepartures = true;
  bool _trackArrivals = false;
  String _selectedIataCode = DEFAULT_IATA;
  String _selectedBounds = DEFAULT_BOUNDS;

  @override
  void initState() {
    _initSettings();
    super.initState();
  }

  @override
  void dispose() {
    _boundsController.dispose();
    super.dispose();
  }

  void _initSettings() async {
    _settingsBloc = SettingsBloc(InitialSettingsState(), rootBundle);
    _settingsBloc.add(LoadSettings());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text("Settings")),
        body: _buildBody()
    );
  }

  Widget _buildBody() {
    return BlocProvider(
        create: (context) => _settingsBloc,
        child:
        BlocBuilder<SettingsBloc, SettingsState>(builder: (context, state) {
          if (state is InitialSettingsState) {
            return _showStatus("Intial State");
          } else if (state is Loading) {
            return _showStatus("Loading");
          } else if (state is SettingsLoaded) {
            return _buildContents(state.settings);
          } else if (state is Error) {
            return _showStatus("Error");
          } else {
            return _showStatus("Unknown");
          }
        }));
  }

  Column _buildContents(SettingsModel settings) {
    _trackDepartures = settings.departuresEnabled;
    _trackArrivals = settings.arrivalsEnabled;
    _airports = settings.iataCodes;
    _boundsController = TextEditingController(text: settings.selectedBounds);
    return Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          _switchPreference(
              "Track departures",
              "Departures will be tracked",
              _trackDepartures,
                  (value) {
                setState(() {
                  _trackDepartures = value;
                  settings.departuresEnabled = value;
                });
                _settingsBloc.add(SaveDeparturesEnabled(value));
              }
          ),
          _switchPreference(
              "Track arrivals",
              "Arrivals will be tracked",
              _trackArrivals,
                  (value) {
                setState(() {
                  _trackArrivals = value;
                  settings.arrivalsEnabled = value;
                });
                _settingsBloc.add(SaveArrivalsEnabled(value));
              }
          ),
          _textPreference(
              "Airport IATA code",
              "Three character IATA code, like WAW",
              "WAW",
                  () => _showAirportDialog()
          ),
          _textPreference(
              "Tracking zone boundaries",
              "Boundaries should be defined as a two pairs of latitude and longitude, like: latitude 1: 57.00, latitude 2: 47.00, longitude 1: 12.00, longitude 2: 26.00",
              "WAW",
                  () => _showBoundsDialog()
          )
        ]
    );
  }

  void _showAirportDialog() async {
    var result = await showDialog<String>(
        context: context,
        builder: (BuildContext context) => _buildAirportDialog()
    );
    _settingsBloc.add(SaveIataCode(result ?? DEFAULT_IATA));
  }

  AlertDialog _buildAirportDialog() {
    return AlertDialog(
      title: const Text("Select airport by IATA code"),
      content: Autocomplete<String>(
        optionsBuilder: (TextEditingValue editingValue) {
          if (editingValue.text == '') {
            return const Iterable<String>.empty();
          }
          return _airports.where((String iata) {
            return iata.contains(editingValue.text.toUpperCase());
          });
        },
        onSelected: (String selection) {
          _selectedIataCode = selection;
        },
      ),
      actions: <Widget>[
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancel'),
        ),
        TextButton(
          onPressed: () => Navigator.pop(context, _selectedIataCode),
          child: const Text('Save'),
        ),
      ],
    );
  }

  void _showBoundsDialog() async {
    var result = await showDialog<String>(
        context: context,
        builder: (BuildContext context) => _buildBoundsDialog()
    );
    _settingsBloc.add(SaveBounds(result ?? DEFAULT_BOUNDS));
  }

  AlertDialog _buildBoundsDialog() {
    return AlertDialog(
      title: const Text("Select bounds"),
      content: TextField(
        controller: _boundsController,
        onChanged: (text) {
          _selectedBounds = text;
        },
      ),
      actions: <Widget>[
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('Cancel'),
        ),
        TextButton(
          onPressed: () => Navigator.pop(context, _selectedBounds),
          child: const Text('Save'),
        ),
      ],
    );
  }

  Widget _switchPreference(String title,
      String description,
      bool value,
      ValueChanged<bool> onChanged) =>
      Padding(
          padding: EdgeInsets.only(left: 64, right: 32, top: 32),
          child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(title, style: TextStyle(fontSize: 18)),
                      Container(height: 2),
                      Text(description, style: TextStyle(color: Colors.white70))
                    ]),
                Switch(
                  value: value,
                  onChanged: onChanged,
                )
              ]
          ));

  Widget _textPreference(String title,
      String description,
      String value,
      Function() onTap) =>
      Padding(
          padding: EdgeInsets.only(left: 64, right: 32, top: 32),
          child: GestureDetector(
              onTap: onTap,
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(title, style: TextStyle(fontSize: 18)),
                    Container(height: 2),
                    Text(description, style: TextStyle(color: Colors.white70))
                  ])
          )
      );

  Widget _showStatus(String status) {
    return Center(
      child:
      Text(status, style: TextStyle(color: Colors.white60, fontSize: 14)),
    );
  }
}
