package com.darekbx.flightssniffer.repository.aircraft

import android.util.Log
import com.darekbx.flightssniffer.repository.AssetProvider
import org.json.JSONArray
import org.json.JSONException

class AircraftInfo(
    private val assetProvider: AssetProvider
) {

    private val aircraftInfoCache = HashMap<String, String>()
    private val bigAircraftCache = mutableListOf<String>()

    fun fetchBigAicraft(): List<String> {
        if (bigAircraftCache.isNotEmpty()) {
            return bigAircraftCache
        }
        val json = assetProvider.loadBigAircraft()
            ?: return emptyList()

        try {
            val array = JSONArray(json)
            for (index in 0 until array.length()) {
                bigAircraftCache.add(array.getString(index))
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Unable to parse big aicraft json", e)
        }

        return bigAircraftCache
    }

    fun fetchAircraftInfo(): HashMap<String, String> {
        if (aircraftInfoCache.isNotEmpty()) {
            return aircraftInfoCache
        }
        val json = assetProvider.loadAircraftInfo()
            ?: return hashMapOf()

        try {
            val array = JSONArray(json)
            for (index in 0 until array.length()) {
                val aircraftObject = array.getJSONObject(index)
                val icao = aircraftObject.getString("icao")
                aircraftInfoCache[icao] = aircraftObject.getString("model")
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Unable to parse aicraft json", e)
        }

        return aircraftInfoCache
    }

    companion object {
        private const val TAG = "AircraftInfo"
    }
}
