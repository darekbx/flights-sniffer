import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutterapp/repository/remote/model/models.dart';
import 'package:flutterapp/ui/flights/bloc/flights_bloc.dart';
import 'package:flutterapp/ui/flights/bloc/flights_event.dart';
import 'package:flutterapp/ui/flights/bloc/flights_state.dart';

class FlightDetailsScreen extends StatefulWidget {
  final String flightId;
  final String callSign;

  const FlightDetailsScreen(this.flightId, this.callSign, { Key? key }) : super(key: key);

  @override
  _FlightDetailsState createState() => _FlightDetailsState();
}

class _FlightDetailsState extends State<FlightDetailsScreen> {

  late FlightsBloc _flightsBloc;

  @override
  void initState() {
    super.initState();
    _loadFlights();
  }

  void _loadFlights() async {
    _flightsBloc = FlightsBloc(InitialFightsState(), rootBundle);
    _flightsBloc.add(LoadFlight(widget.flightId));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text("${widget.callSign} details")),
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
          } else if (state is FlightLoaded) {
            return _displayFlightState(state.flightDetails);
          } else if (state is Error) {
            return _showStatus("Error");
          } else {
            return _showStatus("Unknown");
          }
        }));
  }

  Widget _displayFlightState(FlightDetails flightDetails) {
    var color = flightDetails.statusColor;
    var colorMap = Map<String, MaterialColor>();
    colorMap["grey"] = Colors.grey;
    colorMap["green"] = Colors.green;
    colorMap["yellow"] = Colors.yellow;
    colorMap["orange"] = Colors.orange;
    colorMap["red"] = Colors.red;
    
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
            padding: EdgeInsets.only(left: 8, top: 8),
            child:
            Text(flightDetails.aircraft.model,
                style: TextStyle(fontWeight: FontWeight.bold))
        ),
        Padding(
            padding: EdgeInsets.only(left: 8, bottom: 8),
            child: Text(flightDetails.airline)
        ),
        Image.network(flightDetails.aircraft.imageUrl!),
        Padding(
            padding: EdgeInsets.only(left: 8, top: 8),
            child: Text(
                "${flightDetails.origin.iata} to ${flightDetails.destination
                    .iata}",
                style: TextStyle(fontWeight: FontWeight.bold))
        ),
        Padding(
            padding: EdgeInsets.only(left: 8, bottom: 8),
            child: Text(
                "${flightDetails.origin.name} to ${flightDetails.destination
                    .name}")
        ),
        Padding(
            padding: EdgeInsets.only(left: 8, top: 8, bottom: 8),
            child: Text("${flightDetails.estimatedText}", style: TextStyle(
              color: colorMap[color]
            ))
        )
      ],
    );
  }

  Widget _showStatus(String status) {
    return Center(
      child:
      Text(status, style: TextStyle(color: Colors.white60, fontSize: 14)),
    );
  }
}
