package com.darekbx.flightssniffer.repository

import android.util.Log
import com.darekbx.flightssniffer.repository.flightsinformation.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.awaitResponse
import java.lang.Exception

class FlightsRepository(
    private val flightsInformation: FlightsInformation
) {

    suspend fun loadFlights(bounds: DoubleArray): ResponseWrapper<Flights> {
        try {
            val flights = flightsInformation.flights(bounds.joinToString(separator = ","))
            val response = flights.awaitResponse()
            val responseJson = response.body()

            if (response.isSuccessful && responseJson != null) {
                return ResponseWrapper(parseFlights(responseJson))
            } else {
                return ResponseWrapper(null,
                    "HTTP ${response.code()}, ${response.errorBody()?.string() ?: response.body()}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Unable to load flights", e)
            return ResponseWrapper(null, e.localizedMessage)
        }
    }

    suspend fun loadFlightDetails(flightId: String): ResponseWrapper<FlightDetails> {
        try {
            val flightDetails = flightsInformation.flightDetails(flightId)
            val response = flightDetails.awaitResponse()
            val responseJson = response.body()

            if (response.isSuccessful && responseJson != null) {
                return ResponseWrapper(parseFlightDetails(responseJson))
            } else {
                return ResponseWrapper(null,
                    "HTTP ${response.code()}, ${response.errorBody()?.string() ?: response.body()}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Unable to load flight etails", e)
            return ResponseWrapper(null, e.localizedMessage)
        }
    }

    private fun parseFlights(jsonString: String): Flights? {
        return try {
            val root = JSONObject(jsonString)
            val flightsList = mutableListOf<Flight>()
            root.keys().forEach { key ->
                if (root.get(key) is JSONArray) {
                    val flight = Flight.fromJsonArray(root, key)
                    flightsList.add(flight)
                }
            }
            Flights(flightsList)
        } catch (e: JSONException) {
            Log.e(TAG, "Unable to parse flights json", e)
            null
        }
    }

    private fun parseFlightDetails(jsonString: String): FlightDetails? {
        return try {
            val root = JSONObject(jsonString)

            val estimatedText = root.getJSONObject("status").getString("text")
            val airline = root.getJSONObject("airline").getString("name")

            val airport = root.getJSONObject("airport")
            val origin = airport.getJSONObject("origin").getString("name")
            val destination = airport.getJSONObject("destination").getString("name")
            val aircraft = parseAircraft(root)
            val trail = parseTrail(root)

            FlightDetails(
                estimatedText,
                aircraft,
                airline,
                origin,
                destination,
                trail
            )
        } catch (e: JSONException) {
            Log.e(TAG, "Unable to parse flight details json", e)
            null
        }
    }

    private fun parseAircraft(root: JSONObject): Aircraft {
        val aircraft = root.getJSONObject("aircraft")
        val aircraftModel = aircraft.getJSONObject("model").getString("text")
        val aircraftImages = aircraft.getJSONObject("images").getJSONArray("medium")
        val aircraftImage = if (aircraftImages.length() > 0) {
            aircraftImages.getString(0)
        } else {
            null
        }
        return Aircraft(aircraftModel, aircraftImage)
    }

    private fun parseTrail(root: JSONObject): List<Trail> {
        val trail = mutableListOf<Trail>()
        val items = root.getJSONArray("trail")

        for (index in 0 until items.length()) {
            with(items.getJSONObject(index)) {
                val item = Trail(
                    getDouble("lat"),
                    getDouble("lng"),
                    getInt("alt"),
                    getInt("spd"),
                    getLong("ts"),
                )
                trail.add(item)
            }
        }

        return trail
    }

    companion object {
        private const val TAG = "FlightsRepository"
    }
}