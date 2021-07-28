package com.darekbx.flightssniffer.di

import android.content.Context
import androidx.preference.PreferenceManager
import com.darekbx.flightssniffer.repository.aircraft.AircraftIcons
import com.darekbx.flightssniffer.repository.AssetProvider
import com.darekbx.flightssniffer.repository.airports.AirportsRepository
import com.darekbx.flightssniffer.repository.flightsinformation.FlightsRepository
import com.darekbx.flightssniffer.repository.aircraft.AircraftInfo
import com.darekbx.flightssniffer.repository.flightsinformation.getFlightsInformationService
import org.koin.dsl.module

object CommonModule {
    fun get() = module {
        single { AircraftIcons(get()) }
        single { AssetProvider((get() as Context).assets) }
        single { getFlightsInformationService() }
        single { FlightsRepository(get()) }
        single { AirportsRepository(get(), get()) }
        single { AircraftInfo(get()) }
        single { PreferenceManager.getDefaultSharedPreferences(get()) }
    }
}