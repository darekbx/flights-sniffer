import 'package:flutter_test/flutter_test.dart';
import 'package:flutterapp/repository/remote/flights.dart';
import 'package:http/http.dart';
import 'package:http/testing.dart';

void main() {

  const String FLIGHTS_JSON = """{
"full_count":11910,
"version":4,
"287d85cb":[
  "A26F79",48.122,-117.498,19,54700,14,"4456","F-KSFF3","BALL","N256TH",1627642163,"","","",0,0,"HBAL546",0,""
],
"28996aba":[
  "47843F",50.919,11.628,218,44999,438,"3544","T-MLAT1","LJ45","LN-AWB",1627642158,"HEL","WAW","NWG20",0,0,"NWG20",0,"NWG"
]
}""";

  const String FLIGHT_DETAILS_JSON = """
{
"identification":{},
"status":{"live":true,"text":"Estimated- 15:34","icon":"green"},
"level":"limited",
"promote":false,
"aircraft":{"model":{"code":"A333","text":"Airbus A330-302"},
"countryId":179,
"registration":"A7-AEI",
"hex":"06a04a",
"age":null,
"msn":null,
"images":{"thumbnails":[],"medium":[{"src":"image_src","link":"image_url","copyright":"Ferenc Kolos","source":"Jetphotos"}],"large":[]}},
"airline":{"name":"Qatar Airways"},
"owner":null,
"airspace":null,
"airport":{
  "origin":{"name":"Doha Hamad International Airport","code":{"iata":"DOH","icao":"OTHH"},"visible":true,"website":"web","info":{}},
  "destination":{"name":"Brussels Airport","code":{"iata":"BRU","icao":"EBBR"},"visible":true,"website":"web","info":{},"real":null}
},
"flightHistory":{"aircraft":[]},
"ems":null,
"availability":["AGE","MSN"],
"time":{},
"trail":[{"lat":48.092926,"lng":19.286018,"alt":40000,"spd":425,"ts":1627646637,"hd":306}],
"firstTimestamp":1627628218,
"s":"aaa"
}
""";

  test("Flights were loaded", () async {
    // Given
    final flightsRepository = FlightsRepository();
    flightsRepository.client =
        MockClient((request) async => Response(FLIGHTS_JSON, 200));

    // When
    var flights =
        await flightsRepository.loadFlights([52.00, 41.00, 10.00, 18.00]);

    // Then
    expect(flights, isNotNull);
    expect(flights.length, equals(2));

    var flight = flights[1];
    expect(flight.flightId, "28996aba");
    expect(flight.destination, "WAW");
    expect(flight.origin, "HEL");
    expect(flight.flight, "NWG20");
    expect(flight.rotation, 218);
    expect(flight.lat, 50.919);
    expect(flight.lng, 11.628);
    expect(flight.speed, 438);
    expect(flight.altitude, 44999);
    expect(flight.timestamp, 1627642158);
    expect(flight.callSign, "NWG20");
    expect(flight.icao, "LJ45");
    expect(flight.speedInKmH(), 811);
    expect(flight.altitudeInMeters(), 13715);
    expect(flight.mapRotation(), -173);
  });

  test("Flight details was loaded", () async {
    // Given
    final flightsRepository = FlightsRepository();
    flightsRepository.client =
        MockClient((request) async => Response(FLIGHT_DETAILS_JSON, 200));

    // When
    var flightDetails =
        await flightsRepository.loadFlightDetails("flightId");

    // Then
    expect(flightDetails, isNotNull);
    expect(flightDetails?.estimatedText, equals("Estimated- 15:34"));
    expect(flightDetails?.airline, equals("Qatar Airways"));
    expect(flightDetails?.statusColor, equals("green"));

    expect(flightDetails?.origin?.name, equals("Doha Hamad International Airport"));
    expect(flightDetails?.origin?.iata, equals("DOH"));

    expect(flightDetails?.destination?.name, equals("Brussels Airport"));
    expect(flightDetails?.destination?.iata, equals("BRU"));

    expect(flightDetails?.aircraft?.model, equals("Airbus A330-302"));
    expect(flightDetails?.aircraft?.imageUrl, equals("image_src"));

    expect(flightDetails?.trail[0].lat, equals(48.092926));
    expect(flightDetails?.trail[0].lng, equals(19.286018));
    expect(flightDetails?.trail[0].alt, equals(40000));
    expect(flightDetails?.trail[0].spd, equals(425));
    expect(flightDetails?.trail[0].ts, equals(1627646637));
  });
}
