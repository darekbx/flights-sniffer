import 'package:flutter/material.dart';
import 'package:flutterapp/model/flight.dart';
import 'package:flutterapp/ui/flightdetails/flightdetailsscreen.dart';
import 'package:flutterapp/ui/settings/settingsscreen.dart';

class FlightsScreen extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text("Flights")),
        body:  Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text("Flights Page"),
              MaterialButton(child: Text("Open details"), onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (context) =>
                    FlightDetailsScreen(flight: Flight("aa"))));
              }),
              ElevatedButton(child: Text("Open settings"), onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (context) =>
                    SettingsScreen()));
              }),
            ])
    );
  }
}
