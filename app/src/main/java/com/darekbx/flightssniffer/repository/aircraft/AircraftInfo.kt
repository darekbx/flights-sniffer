package com.darekbx.flightssniffer.repository.aircraft

import android.util.Log
import com.darekbx.flightssniffer.repository.AssetProvider
import org.json.JSONArray
import org.json.JSONException

class AircraftInfo(
    private val assetProvider: AssetProvider
) {

    private val cache = HashMap<String, String>()

    fun fetchAircraftInfo(): HashMap<String, String> {
        if (cache.isNotEmpty()) {
            return cache
        }
        val json = assetProvider.loadAircraftInfo()
            ?: return hashMapOf()

        try {
            val array = JSONArray(json)
            for (index in 0 until array.length()) {
                val aircraftObject = array.getJSONObject(index)
                cache.put(
                    aircraftObject.getString("icao"),
                    aircraftObject.getString("model")
                )
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Unable to parse aicraft json", e)
        }

        return cache
    }

    companion object {
        private const val TAG = "AircraftInfo"
    }
}
