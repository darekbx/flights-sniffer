package com.darekbx.flightssniffer.repository.airportinformation

import android.util.Log
import com.darekbx.flightssniffer.repository.aircraft.AircraftInfo
import com.darekbx.flightssniffer.repository.airports.AirportModel
import com.darekbx.flightssniffer.repository.airports.AirportsRepository
import com.google.gson.Gson

class AirportInformationRepository(
    private val airportInformation: AirportInformation,
    private val airportsRepository: AirportsRepository,
    private val aircraftInfo: AircraftInfo,
    private val gson: Gson
) {

    fun searchForBigAircraft(): List<Flight> {
        val aircraftModels = mutableListOf<Flight>()
        val aircraftNotifications = aircraftInfo.fetchAicraftNotifications()
        Log.v(TAG, "Loaded aircraft codes for notifications, count: ${aircraftNotifications.size}")

        airportsRepository.selectedAirport()?.let { selectedAirport ->
            Log.v(TAG, "Target airport: ${selectedAirport.name}")

            AirportInformation.Type.values().forEach { type ->
                val results = searchForAircraft(
                    type,
                    selectedAirport,
                    aircraftNotifications
                )
                aircraftModels.addAll(results)
            }
        }

        return aircraftModels.toList()
    }

    private fun searchForAircraft(
        type: AirportInformation.Type,
        selectedAirport: AirportModel,
        aircraftNotifications: List<String>
    ): List<Flight> {
        val aircraftModels = mutableListOf<Flight>()
        val airport = selectedAirport.iataCode
        val time = System.currentTimeMillis() / 1000
        var page = 1
        // Just something bigger than first page index, will be updated after first call
        var maxPages = 2

        Log.v(TAG, "Start to fetch ${type.typeName}")

        do {
            Log.v(TAG, "Fetch page #$page")
            makeApiCall(type, airport, page, time)?.let { wrapper ->
                val response = when (type) {
                    AirportInformation.Type.ARRIVALS -> wrapper.result.response.airport.pluginData.schedule.arrivals
                    AirportInformation.Type.DEPARTURES -> wrapper.result.response.airport.pluginData.schedule.departures
                }

                Log.v(TAG, "Fetched ${response.data.size} of ${response.item.total} in ${type.typeName}")
                for (flightWrapper in response.data) {
                    val flight = flightWrapper.flight
                    val aircraftCode = flight.aircraft?.model?.code

                    if (aircraftCode != null && aircraftNotifications.contains(aircraftCode)) {
                        aircraftModels.add(flight.apply { airportType = type })
                        Log.v(TAG, "Found big aircraft! ${flight.aircraft.model.text}")
                    }
                }

                // Update max pages for next call
                maxPages = response.page.total

                Log.v(TAG, "Fetched page $page page, update max pages to $maxPages")

                page++
            }
        } while (page <= maxPages)

        return aircraftModels
    }

    private fun makeApiCall(
        type: AirportInformation.Type,
        airport: String,
        page: Int,
        time: Long
    ): Wrapper? {
        val responseCall = airportInformation.flights(airport, page, LIMIT, time, type).execute()
        return when(responseCall.isSuccessful) {
            true -> gson.fromJson(responseCall.body(), Wrapper::class.java)
            else -> null
        }
    }

    companion object {
        private const val TAG = "AirportInformationRepository"

        private const val LIMIT = 100
    }
}