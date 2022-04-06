package com.darekbx.flightssniffer.repository.airportinformation

import android.util.Log
import com.darekbx.flightssniffer.repository.aircraft.AircraftInfo
import com.darekbx.flightssniffer.repository.airports.AirportsRepository
import com.google.gson.Gson

class AirportInformationRepository(
    val airportInformation: AirportInformation,
    val airportsRepository: AirportsRepository,
    val aircraftInfo: AircraftInfo,
    val gson: Gson
) {

    fun searchForBigAircraft(): List<Flight> {
        val aircraftModels = mutableListOf<Flight>()
        val aircraftNotifications = aircraftInfo.fetchAicraftNotifications()
        Log.v(TAG, "Loaded aircraft codes for notifications, count: ${aircraftNotifications.size}")

        airportsRepository.selectedAirport()?.let { selectedAirport ->
            Log.v(TAG, "Target airport: ${selectedAirport.name}")
            val airport = selectedAirport.iataCode
            val time = System.currentTimeMillis() / 1000
            var page = 1
            var maxPages = 2 // Just something bigger than first page index, will be updated after first call

            do {
                Log.v(TAG, "Fetch page #$page")
                makeApiCall(airport, page, time)?.let { wrapper ->
                    val arrivals = wrapper.result.response.airport.pluginData.schedule.arrivals

                    for (flightWrapper in arrivals.data) {
                        val flight = flightWrapper.flight
                        val aircraftCode = flight.aircraft?.model?.code

                        if (aircraftCode != null && aircraftNotifications.contains(aircraftCode)) {
                            aircraftModels.add(flight)
                            Log.v(TAG, "Found big aircraft! ${flight.aircraft.model.text}")
                        }
                    }

                    // Update max pages for next call
                    maxPages = arrivals.page.total
                    page++

                    Log.v(TAG, "Update max pages to $maxPages")
                }
                page++
            } while (page <= maxPages)
        }

        return aircraftModels.toList()
    }

    private fun makeApiCall(airport: String, page: Int, time: Long): Wrapper? {
        val responseCall = airportInformation.flights(airport, page, LIMIT, time, TYPE).execute()
        return when(responseCall.isSuccessful) {
            true -> gson.fromJson(responseCall.body(), Wrapper::class.java)
            else -> null
        }
    }

    companion object {
        private const val TAG = "AirportInformationRepository"

        private const val LIMIT = 100
        private val TYPE = AirportInformation.Type.ARRIVALS
    }
}