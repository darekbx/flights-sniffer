import 'package:flutter/material.dart';
import 'package:flutterapp/model/flight.dart';
import 'package:flutterapp/ui/flightdetails/flightdetailsscreen.dart';
import 'package:flutterapp/ui/flights/flightsmap.dart';
import 'package:flutterapp/ui/settings/settingsscreen.dart';

class FlightsScreen extends StatelessWidget {
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
        body: Column(
            crossAxisAlignment: CrossAxisAlignment.center, children: [

          /**
           * Update map in every 10s? bloc is just streaming like flow
           *
           */
          FlightsMap(),


          Text("Flights Page"),
          MaterialButton(
              child: Text("Open details"),
              onPressed: () {
                Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) =>
                            FlightDetailsScreen(flight: Flight("aa"))));
              })
        ]));
  }
}
