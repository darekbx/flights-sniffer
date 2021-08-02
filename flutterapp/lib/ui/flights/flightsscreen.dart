import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutterapp/ui/flights/bloc/flights_bloc.dart';
import 'package:flutterapp/ui/flights/bloc/flights_event.dart';
import 'package:flutterapp/ui/flights/bloc/flights_state.dart';
import 'package:flutterapp/ui/flights/flightsmap.dart';
import 'package:flutterapp/ui/settings/settingsscreen.dart';

class FlightsScreen extends StatefulWidget {
  FlightsScreen({key}) : super(key: key);

  @override
  _FlightsScreenState createState() => _FlightsScreenState();
}

class _FlightsScreenState extends State<FlightsScreen> {

  late FlightsBloc _flightsBloc;

  @override
  void initState() {
    super.initState();
    _loadFlights();
  }

  void _loadFlights() async {
    _flightsBloc = FlightsBloc(InitialFightsState(), rootBundle);
    _flightsBloc.add(ObserveFlights());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text("Flights"),
          leading: GestureDetector(
              child: Icon(Icons.settings),
              onTap: () {
                Navigator.push(context,
                    MaterialPageRoute(builder: (context) => SettingsScreen()));
              }),
        ),
        body: _buildBody());
  }

  Widget _buildBody() {
    return BlocProvider(
        create: (context) => _flightsBloc,
        child:
            BlocBuilder<FlightsBloc, FlightsState>(builder: (context, state) {
          if (state is InitialFightsState) {

            return _showStatus("Intial State");
          } else if (state is Loading) {
            return _showStatus("Loading");
          } else if (state is FlightsLoaded) {
            return _showFlights(state);
          } else if (state is Error) {
            return _showStatus("Error");
          } else {
            return _showStatus("Unknown");
          }
        }));
  }

  Widget _showFlights(FlightsLoaded state) {
    return Column(crossAxisAlignment: CrossAxisAlignment.center, children: [
      /**
           * Update map in every 10s? bloc is just streaming like flow
           *
           */
      FlightsMap(),
      ListView.builder(
          itemCount: state.flights.length,
          itemBuilder: (context, index) {
            var flight = state.flights[index];
            return ListTile(
              leading: flight.icon != null ? Image.memory(flight.icon!) : Container(),
              title: Text(flight.aircraftName),
              subtitle: Text("${flight.origin} to ${flight.destination}"),
              trailing: Text("${flight.speed}"),
            );

      })
    ]);
  }

  Widget _showStatus(String status) {
    return Center(
      child:
          Text(status, style: TextStyle(color: Colors.black87, fontSize: 14)),
    );
  }
}
