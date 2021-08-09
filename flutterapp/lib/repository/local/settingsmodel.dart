
class SettingsModel {

  final List<String> iataCodes;
  bool departuresEnabled;
  bool arrivalsEnabled;
  final String selectedIataCode;
  final String selectedBounds;

  SettingsModel(this.iataCodes,
      this.departuresEnabled,
      this.arrivalsEnabled,
      this.selectedIataCode,
      this.selectedBounds);
}
