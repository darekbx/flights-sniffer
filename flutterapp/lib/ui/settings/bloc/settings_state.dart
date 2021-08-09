import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'package:flutterapp/repository/local/settingsmodel.dart';

@immutable
abstract class SettingsState extends Equatable {
  SettingsState([List props = const []]);

  @override
  List<Object> get props => [];
}

class InitialSettingsState extends SettingsState { }

class Loading extends SettingsState { }

class SettingsLoaded extends SettingsState {
  final SettingsModel settings;

  SettingsLoaded(this.settings) : super([settings]);
}

class Error extends SettingsState {
  final String message;

  Error(this.message) : super([message]);
}
