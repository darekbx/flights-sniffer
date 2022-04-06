package com.darekbx.flightssniffer.di

import android.content.Context
import androidx.preference.PreferenceManager
import com.darekbx.flightssniffer.repository.aircraft.AircraftIcons
import com.darekbx.flightssniffer.repository.AssetProvider
import com.darekbx.flightssniffer.repository.airports.AirportsRepository
import com.darekbx.flightssniffer.repository.flightsinformation.FlightsRepository
import com.darekbx.flightssniffer.repository.aircraft.AircraftInfo
import com.darekbx.flightssniffer.repository.airportinformation.AirportInformationRepository
import com.darekbx.flightssniffer.repository.airportinformation.getAirportInformationService
import com.darekbx.flightssniffer.repository.flightsinformation.getFlightsInformationService
import com.google.gson.Gson
import org.koin.dsl.module

object CommonModule {
    fun get() = module {
        single { AircraftIcons(get()) }
        single { AssetProvider((get() as Context).assets) }
        single { getFlightsInformationService() }
        single { getAirportInformationService() }
        single { FlightsRepository(get()) }
        single { AirportsRepository(get(), get()) }
        single { AirportInformationRepository(get(), get(), get(), get()) }
        single { AircraftInfo(get()) }
        single { Gson() }
        single { PreferenceManager.getDefaultSharedPreferences(get()) }
    }
}