package com.darekbx.flightssniffer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.flightssniffer.repository.airports.AirportModel
import com.darekbx.flightssniffer.repository.airports.AirportsRepository
import kotlinx.coroutines.launch

class AirportsViewModel(
    private val airportsRepository: AirportsRepository
) : ViewModel() {

    fun loadAirports(): LiveData<List<AirportModel>> =
        MutableLiveData<List<AirportModel>>().apply {
            viewModelScope.launch {
                val airports = airportsRepository.loadAirports()
                postValue(airports)
            }
        }
}