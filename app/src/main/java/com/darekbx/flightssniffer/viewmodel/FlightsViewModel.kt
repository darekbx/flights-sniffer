package com.darekbx.flightssniffer.viewmodel

import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.flightssniffer.repository.aircraft.AircraftIcons
import com.darekbx.flightssniffer.repository.airports.AirportsRepository
import com.darekbx.flightssniffer.repository.flightsinformation.FlightsRepository
import com.darekbx.flightssniffer.repository.aircraft.AircraftInfo
import com.darekbx.flightssniffer.repository.airports.AirportModel
import com.darekbx.flightssniffer.repository.flightsinformation.Flight
import com.darekbx.flightssniffer.repository.flightsinformation.FlightDetails
import com.darekbx.flightssniffer.ui.settings.SettingsFragment
import com.darekbx.flightssniffer.ui.settings.SettingsFragment.Companion.DEFAULT_BOUNDS
import com.darekbx.flightssniffer.ui.settings.SettingsFragment.Companion.toBounds
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class FlightsViewModel(
    private val aircraftIcons: AircraftIcons,
    private val aircraftInfo: AircraftInfo,
    private val flightsRepository: FlightsRepository,
    private val airportsRepository: AirportsRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var aircraftInfoMap = HashMap<String, String>()
    private var bigAircraftList = listOf<String>()

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

    private val _flightDetails = MutableLiveData<FlightDetails>()
    val flightDetails: LiveData<FlightDetails>
        get() = _flightDetails

    fun loadDetails(flightId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val wrapper = flightsRepository.loadFlightDetails(flightId)
            if (wrapper.response != null) {
                _flightDetails.postValue(wrapper.response!!)
            } else {
                _errorMessage.postValue(wrapper.errorMessage ?: "Unknown error")
            }
            _isLoading.postValue(false)
        }
    }

    fun loadStatus() {
        _isLoading.value = true
        viewModelScope.launch {
            aircraftInfoMap = aircraftInfo.fetchAircraftInfo()
            bigAircraftList = aircraftInfo.fetchBigAicraft()
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
                .filterByAirport()
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
            if (aircraftInfoMap[flight.icao] != null) {
                flight.aircraftName = "${aircraftInfoMap[flight.icao]} (${flight.icao})"
            }
            flight.isBigPlane = bigAircraftList.contains(flight.icao)
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

    private fun List<Flight>.filterByAirport(): List<Flight> =
        filter {
            val byDestination = filterByDestination()
            val byOrigin = filterByOrigin()
            val airportIata = activeAirport.value!!.iataCode
            when {
                byDestination && byOrigin -> {
                    it.destination == airportIata || it.origin == airportIata
                }
                byDestination -> it.destination == airportIata
                byOrigin -> it.origin == airportIata
                else -> false
            }
        }

    private fun List<Flight>.filterArrived(): List<Flight> =
        filter { it.speed != 0 && it.altitude != 0 }

    private val flightBounds by lazy {
        val bounds = sharedPreferences.getString(SettingsFragment.ZONE_BOUNDARIES, null)
            ?: DEFAULT_BOUNDS
        try {
            bounds.toBounds()
        } catch (e: NumberFormatException) {
            DEFAULT_BOUNDS.toBounds()
        }
    }

    private fun filterByDestination() =
        sharedPreferences.getBoolean(SettingsFragment.TRACK_ARRIVALS, false)

    private fun filterByOrigin() =
        sharedPreferences.getBoolean(SettingsFragment.TRACK_DEPARTURES, false)
}