package com.darekbx.flightssniffer.repository.flightsinformation

import android.graphics.Bitmap
import org.json.JSONObject

data class Flights(val list: List<Flight>)

/**
 * "28792892":[ // flight internal id
 *   0  "4B8DE8",
 *   1  52.573, // Lat
 *   2  14.025, // Lng
 *   3  119, // hd?
 *   4  37000, // altitude
 *   5  461, // speed
 *   6  "7620",
 *   7  "F-ETNU1",
 *   8  "B738", // icao
 *   9  "TC-COH",
 *   10 1626774367, // timestamp
 *   11 "HAM", // origin
 *   12 "AYT", // destination
 *   13 "XC2536", // flight id
 *   14 0,
 *   15 64,
 *   16 "CAI53HE", // call sign
 *   17 0,
 *   18 ""]
 */
data class Flight(
    val flightId: String,
    val destination: String,
    val origin: String,
    val icao: String,
    val flight: String,
    val callSign: String,
    val timestamp: Long,
    val altitude: Int,
    val speed: Int, // In knots
    val lat: Double,
    val lng: Double
) {

    var icon: Bitmap? = null
    var distanceLeft: Int = Int.MAX_VALUE

    val speedInKmH = (speed.toDouble() * 1.85200).toInt()
    val altitudeInMeters = (altitude.toDouble() * 0.3048).toInt()

    companion object {
        fun fromJsonArray(jsonObject: JSONObject, flightId: String): Flight {
            val params = jsonObject.getJSONArray(flightId)
            return Flight(
                flightId = flightId,
                destination = params.getString(12),
                origin = params.getString(11),
                icao = params.getString(8),
                flight = params.getString(13),
                callSign = params.getString(16),
                timestamp = params.getLong(10),
                altitude = params.getInt(4),
                speed = params.getInt(5),
                lat = params.getDouble(1),
                lng = params.getDouble(2)
            )
        }
    }
}

data class FlightDetails(
    val estimatedText: String, // status.text
    val aircraft: Aircraft,
    val airline: String, // airline.name
    val origin: String, // airport.origin.name
    val destination: String, // airport.destination.name
    val trail: List<Trail>
)

data class Aircraft(
    val model: String, // aircraft.model.text
    val imageUrl: String? // aircraft.images.medium[0].src
)

data class Trail(
    val lat: Double, // trail[x].lat
    val lng: Double, // trail[x].lng
    val alt: Int, // trail[x].alt
    val spd: Int, // trail[x].spd
    val ts: Long // trail[x].ts
)
