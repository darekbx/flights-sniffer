package com.darekbx.flightssniffer.repository.airports

import android.util.Log
import com.darekbx.flightssniffer.repository.AssetProvider
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AirportsRepository(
    private val assetProvider: AssetProvider
) {

    private val cache = mutableListOf<AirportModel>()

    fun selectedAirport(): AirportModel? {
        val airports = loadAirports()
        return airports.firstOrNull { it.iataCode == selectedAirport }
    }

    private fun loadAirports(): List<AirportModel> {
        if (cache.isNotEmpty()) {
            return cache
        }
        val json = assetProvider.loadAirports()
            ?: return emptyList()
        try {
            val array = JSONArray(json)
            for (index in 0 until array.length()) {
                val jsonObject = array.getJSONObject(index)
                cache.add(jsonObject.toAirportModel())
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Unable to parse airports json", e)
        }
        return cache
    }

    private fun JSONObject.toAirportModel(): AirportModel {
        val name = getString("name")
        val countryCode = getString("iso_country")
        val iataCode = getString("iata_code")
        val coordinates = getString("coordinates").split(", ")
        val lat = coordinates[1].toDouble()
        val lng = coordinates[0].toDouble()
        return AirportModel(iataCode, countryCode, name, lat, lng)
    }

    private val selectedAirport by lazy {
        // TODO use value from settings
        "WAW"
    }

    companion object {
        private const val TAG = "AirportsRepository"
    }
}