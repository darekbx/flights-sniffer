import 'package:flutter/material.dart';

class FlightDetailsScreen extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text("Flight flightId")),
        body:  Center(
          child: Text("FlightId: flightId"),
        ));
  }
}
