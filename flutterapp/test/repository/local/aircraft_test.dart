import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutterapp/repository/local/aircraft.dart';
import 'package:mockito/annotations.dart';

class MockAssetBundle extends Fake implements AssetBundle {
  Future<String> loadString(String key, {bool cache = true}) {
    if (key == "big_aircraft.json") {
      return Future.value("""["B788", "A380", "B787"]""");
    } else {
      return Future.value(
          """[{"model": "Airbus A350-1000","type": "Airplane","icao": "A35K","iata": "351"}]""");
    }
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

  test("Big aircraft list was loaded", () async {
    // Given
    final aircraftRepository = AircraftRepository(assetBundle);

    // When
    var bigAircraft = await aircraftRepository.loadBigAircraft();

    // Then
    expect(bigAircraft.length, 3);
    expect(bigAircraft, ["B788", "A380", "B787"]);
  });

  test("Aircraft dictionary was loaded", () async {
    // Given
    final aircraftRepository = AircraftRepository(assetBundle);

    // When
    var aircraftDictionary = await aircraftRepository.loadAircraftDictionary();

    // Then
    expect(aircraftDictionary.length, 1);
    expect(aircraftDictionary.containsKey("A35K"), true);
    expect(aircraftDictionary["A35K"], "Airbus A350-1000");
  });
}
