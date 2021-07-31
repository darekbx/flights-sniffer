class AirportModel {
  final String iataCode;
  final String countryCode;
  final String name;
  final double lat;
  final double lng;

  AirportModel({
    required this.iataCode,
    required this.countryCode,
    required this.name,
    required this.lat,
    required this.lng
  });

  factory AirportModel.fromJson(Map<String, dynamic> json) {
    var coordinates = json['coordinates'].toString().split(", ");
    return AirportModel(
        iataCode: json['iata_code'],
        countryCode: json['iso_country'],
        name: json['name'],
        lat: double.parse(coordinates[1]),
        lng: double.parse(coordinates[0])
    );
  }
}
