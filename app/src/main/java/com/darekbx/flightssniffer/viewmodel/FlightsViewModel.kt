package com.darekbx.flightssniffer.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.flightssniffer.aircraft.AircraftInfo
import com.darekbx.flightssniffer.repository.AirportsRepository
import com.darekbx.flightssniffer.repository.FlightsRepository
import com.darekbx.flightssniffer.repository.airports.AirportModel
import com.darekbx.flightssniffer.repository.flightsinformation.Flight
import kotlinx.coroutines.launch

class FlightsViewModel(
    private val aircraftInfo: AircraftInfo,
    private val flightsRepository: FlightsRepository,
    private val airportsRepository: AirportsRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun loadFlights(): LiveData<List<Flight>> {
        return MutableLiveData<List<Flight>>().apply {
            _isLoading.value = true
            viewModelScope.launch {
                val flights = flightsRepository.loadFlights(flightBounds)
                if (flights != null) {

                    val filtered = flights.list
                        .filterByDestination()
                        .filterArrived()
                        .also {
                            loadIcons(it)
                            loadDistances(it)
                        }
                        .sortByNearest()

                    postValue(filtered)
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun activeAirport(): LiveData<AirportModel> = MutableLiveData(activeAirport)

    private fun List<Flight>.sortByNearest(): List<Flight> =
        sortedBy { it.distanceLeft }

    private fun loadIcons(filtered: List<Flight>) {
        filtered.forEach { flight ->
            flight.icon = aircraftInfo.loadAircraftIcon(flight.icao)
        }
    }

    private fun loadDistances(filtered: List<Flight>) {
        val oneKilometer = 1000
        filtered.forEach { flight ->
            val result = FloatArray(1)
            Location.distanceBetween(
                flight.lat, flight.lng,
                activeAirport.lat, activeAirport.lng,
                result
            )
            flight.distanceLeft = result[0].toInt() / oneKilometer
        }
    }

    private fun List<Flight>.filterByDestination(): List<Flight> =
        filter { it.destination == activeAirport.code }

    private fun List<Flight>.filterArrived(): List<Flight> =
        filter { it.speed != 0 && it.altitude != 0}

    private val flightBounds by lazy {
        // TODO use bounds from settings
        doubleArrayOf(57.00, 47.00, 12.00, 26.00)
    }

    private val activeAirport by lazy { airportsRepository.activeAirport() }
}
