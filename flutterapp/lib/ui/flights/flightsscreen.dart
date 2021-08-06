import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutterapp/ui/flights/bloc/flights_bloc.dart';
import 'package:flutterapp/ui/flights/bloc/flights_event.dart';
import 'package:flutterapp/ui/flights/bloc/flights_state.dart';
import 'package:flutterapp/ui/flights/flightwidget.dart';
import 'package:flutterapp/ui/settings/settingsscreen.dart';
import 'dart:ui' as ui;


class FlightsScreen extends StatefulWidget {
  FlightsScreen({key}) : super(key: key);

  @override
  _FlightsScreenState createState() => _FlightsScreenState();
}

class _FlightsScreenState extends State<FlightsScreen> {

  late ui.Image planeSprites;
  late FlightsBloc _flightsBloc;

  @override
  void initState() {
    super.initState();
    _loadFlights();
  }

  void _loadFlights() async {
    _flightsBloc = FlightsBloc(InitialFightsState(), rootBundle);
    _flightsBloc.add(LoadFlights());

    var assetBytes = await DefaultAssetBundle.of(context).load(
        "assets/aircraft_sprite.png");

    Completer<ui.Image> completer = Completer();
    Image
        .memory(assetBytes.buffer.asUint8List())
        .image
        .resolve(ImageConfiguration())
        .addListener(ImageStreamListener((ImageInfo imageInfo, bool _) {
          completer.complete(imageInfo.image);
    }));

    planeSprites = await completer.future;
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
        body: _buildBody()
    );
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
            return _displayFlightsState(state);
          } else if (state is Error) {
            return _showStatus("Error");
          } else {
            return _showStatus("Unknown");
          }
        }));
  }

  Widget _displayFlightsState(FlightsLoaded state) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        Row(
          children: [
            Expanded(child: Text("{airport}", textAlign: TextAlign.center)),
            GestureDetector(
              child:
              Padding(padding: EdgeInsets.all(8), child: Icon(Icons.refresh)),
              onTap: () {
                _flightsBloc.add(LoadFlights());
              },
            )
          ],
        ),
        Container(
            height: 360,
            child: _showFlights(state)
        )
      ],
    );
  }

  Widget _showFlights(FlightsLoaded state) {
    return ListView.builder(
        itemCount: state.flights.length,
        itemBuilder: (context, index) {
          var flight = state.flights[index];
          return FlightWidget(flight, planeSprites);
        });
  }

  Widget _showStatus(String status) {
    return Center(
      child:
          Text(status, style: TextStyle(color: Colors.white60, fontSize: 14)),
    );
  }
}
