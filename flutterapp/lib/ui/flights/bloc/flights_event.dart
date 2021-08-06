import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

@immutable
abstract class FlightsEvent extends Equatable {
  const FlightsEvent([List props = const []]);

  @override
  List<Object> get props => [];
}

class LoadFlights extends FlightsEvent {
  LoadFlights() : super([]);
}
