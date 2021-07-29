import 'package:flutter/material.dart';
import 'package:flutterapp/model/flight.dart';

class FlightDetailsScreen extends StatelessWidget {
  final Flight flight;

  FlightDetailsScreen({
    required this.flight
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text("Flight ${flight.flightId}")),
        body:  Center(
          child: Text("FlightId: ${flight.flightId}"),
        ));
  }
}
