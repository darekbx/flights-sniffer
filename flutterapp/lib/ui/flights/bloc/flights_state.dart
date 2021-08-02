import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'package:flutterapp/repository/remote/model/models.dart';

@immutable
abstract class FlightsState extends Equatable {
  FlightsState([List props = const []]);

  @override
  List<Object> get props => [];
}

class InitialFightsState extends FlightsState { }

class Loading extends FlightsState { }

class FlightsLoaded extends FlightsState {
  final List<Flight> flights;
  final String airportName;

  FlightsLoaded(this.flights, this.airportName) : super([flights, airportName]);
}

class Error extends FlightsState {
  final String message;

  Error(this.message) : super([message]);
}
