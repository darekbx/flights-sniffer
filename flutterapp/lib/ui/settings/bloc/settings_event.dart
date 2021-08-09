import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

@immutable
abstract class SettingsEvent extends Equatable {
  const SettingsEvent([List props = const []]);

  @override
  List<Object> get props => [];
}

class LoadSettings extends SettingsEvent {
  LoadSettings() : super([]);
}

class SaveDeparturesEnabled extends SettingsEvent {
  final bool isEnabled;

  SaveDeparturesEnabled(this.isEnabled) : super([isEnabled]);
}

class SaveArrivalsEnabled extends SettingsEvent {
  final bool isEnabled;

  SaveArrivalsEnabled(this.isEnabled) : super([isEnabled]);
}

class SaveIataCode extends SettingsEvent {
  final String iataCode;

  SaveIataCode(this.iataCode) : super([iataCode]);
}

class SaveBounds extends SettingsEvent {
  final String bounds;

  SaveBounds(this.bounds) : super([bounds]);
}
