import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutterapp/repository/airports.dart';
import 'package:mockito/annotations.dart';

class MockAssetBundle extends Fake implements AssetBundle {
  Future<String> loadString(String key, {bool cache = true}) {
    return Future.value("""
[
 {
   "name": "Anaa Airport",
   "iso_country": "PF",
   "iata_code": "AAA",
   "coordinates": "-145.50999450683594, -17.35260009765625"
 },
 {
   "name": "Arrabury Airport",
   "iso_country": "AU",
   "iata_code": "AAB",
   "coordinates": "141.047771, -26.693023"
 }
]""");
  }
}

@GenerateMocks([
  AssetBundle
], customMocks: [
  MockSpec<AssetBundle>(as: #MockCatRelaxed, returnNullOnMissingStub: true)
])
void main() {
  late AssetBundle assetBundle;

  setUp(() {
    assetBundle = MockAssetBundle();
  });

  test("Airports were loaded", () async {
    // Given
    final airportRepository = AirportRepository(assetBundle);

    // When
    var airports = await airportRepository.loadAirports();

    // Then
    expect(airports.length, 2);

    var first = airports[0];
    expect(first.iataCode, "AAA");
    expect(first.name, "Anaa Airport");
    expect(first.countryCode, "PF");
    expect(first.lat, -17.35260009765625);
    expect(first.lng, -145.50999450683594);

    var second = airports[1];
    expect(second.iataCode, "AAB");
    expect(second.name, "Arrabury Airport");
    expect(second.countryCode, "AU");
    expect(second.lat, -26.693023);
    expect(second.lng, 141.047771);
  });
}
