import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

@immutable
abstract class FightsState extends Equatable {
  FightsState([List props = const []]);

  @override
  List<Object> get props => [];
}

class InitialFightsState extends FightsState { }

class Loading extends FightsState { }

class FlightsLoaded extends FightsState {
  final List<dynamic /* TODO apply model */> flights;

  FlightsLoaded(this.flights) : super([flights]);
}

class Error extends FightsState {
  final String message;

  Error(this.message) : super([message]);
}
