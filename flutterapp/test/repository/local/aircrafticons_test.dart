import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutterapp/repository/local/aircrafticons.dart';
import 'package:mockito/annotations.dart';

class MockAssetBundle extends Fake implements AssetBundle {
  Future<String> loadString(String key, {bool cache = true}) {
    return Future.value("""
{
	"rotationDegrees":15,
	"url":"1615461067\/t-sprite_c-yellow_w-35_s-yes.png",
	"w":1192,
	"h":1650,
	"icons":{
		"FGTR":{"rotates":true,"aliases":["A4","EUFI"],"frames":[{"45":{"x":82,"y":0,"w":29,"h":29}}]},
		"AS20":{"rotates":true,"aliases":["GLID","G109","SF25"],"frames":[{"45":{"x":84,"y":55,"w":25,"h":25}}]}
	}
}    
""");
  }

  Future<ByteData> load(String key) async {
    var dir = Directory.current;
    var bytes = await File('${dir.path}/assets/aircraft_sprite.png').readAsBytes();
    return ByteData.view(bytes.buffer);
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

  test("Aircraft icon was loaded", () async {
    // Given
    final aircraftIconsRepository = AircraftIconsRepository(assetBundle);

    // When
    var icon = await aircraftIconsRepository.loadAircraftIcon("G109");

    // Then
    expect(icon, isNotNull);
    expect(icon.width, equals(25));
    expect(icon.height, equals(25));
  });
}
