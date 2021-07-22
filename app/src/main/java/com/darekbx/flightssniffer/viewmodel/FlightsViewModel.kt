package com.darekbx.flightssniffer.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.flightssniffer.repository.aircraft.AircraftIcons
import com.darekbx.flightssniffer.repository.airports.AirportsRepository
import com.darekbx.flightssniffer.repository.FlightsRepository
import com.darekbx.flightssniffer.repository.aircraft.AircraftInfo
import com.darekbx.flightssniffer.repository.airports.AirportModel
import com.darekbx.flightssniffer.repository.flightsinformation.Flight
import kotlinx.coroutines.launch

class FlightsViewModel(
    private val aircraftIcons: AircraftIcons,
    private val aircraftInfo: AircraftInfo,
    private val flightsRepository: FlightsRepository,
    private val airportsRepository: AirportsRepository
) : ViewModel() {

    private var aircraftInfoMap = HashMap<String, String>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _activeAirport = MutableLiveData<AirportModel>()
    val activeAirport: LiveData<AirportModel>
        get() = _activeAirport

    private val _flights = MutableLiveData<List<Flight>>()
    val flights: LiveData<List<Flight>>
        get() = _flights

    fun loadStatus() {
        _isLoading.value = true
        viewModelScope.launch {
            aircraftInfoMap = aircraftInfo.fetchAircraftInfo()
            airportsRepository.selectedAirport()?.let { airport ->
                _activeAirport.postValue(airport)
                loadFlights()
            }
            _isLoading.postValue(false)
        }
    }

    private suspend fun loadFlights() {
        val wrapper = flightsRepository.loadFlights(flightBounds)
        if (wrapper.response != null) {
            val data = wrapper.response.list
                .filterByDestination()
                .filterArrived()
                .also {
                    loadIcons(it)
                    loadInfo(it)
                    loadDistances(it)
                }
                .sortByNearest()
            _flights.postValue(data)
        } else {
            _errorMessage.postValue(wrapper.errorMessage ?: "Unknown error")
        }
    }

    private fun List<Flight>.sortByNearest(): List<Flight> =
        sortedBy { it.distanceLeft }

    private fun loadIcons(filtered: List<Flight>) {
        filtered.forEach { flight ->
            flight.icon = aircraftIcons.loadAircraftIcon(flight.icao)
        }
    }

    private fun loadInfo(filtered: List<Flight>) {
        filtered.forEach { flight ->
            flight.aircraftName = aircraftInfoMap[flight.icao]
        }
    }

    private fun loadDistances(filtered: List<Flight>) {
        val oneKilometer = 1000
        filtered.forEach { flight ->
            val result = FloatArray(1)
            Location.distanceBetween(
                flight.lat, flight.lng,
                activeAirport.value!!.lat, activeAirport.value!!.lng,
                result
            )
            flight.distanceLeft = result[0].toInt() / oneKilometer
        }
    }

    private fun List<Flight>.filterByDestination(): List<Flight> =
        filter { it.destination == activeAirport.value!!.iataCode }

    private fun List<Flight>.filterArrived(): List<Flight> =
        filter { it.speed != 0 && it.altitude != 0}

    private val flightBounds by lazy {
        // TODO use bounds from settings
        doubleArrayOf(57.00, 47.00, 12.00, 26.00)
    }
}