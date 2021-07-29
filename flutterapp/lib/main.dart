import 'package:flutter/material.dart';
import 'ui/flights/flightsscreen.dart';

void main() {
  runApp(FlightsSnifferApp());
}

class FlightsSnifferApp extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _FlightsSnifferAppState();
}

class _FlightsSnifferAppState extends State<FlightsSnifferApp> {

  @override
  Widget build(BuildContext context) {
    return  MaterialApp(
      title: "Flights Sniffer",
      home: FlightsScreen()
    );
  }
}
