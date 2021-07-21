package com.darekbx.flightssniffer.di

import android.content.Context
import com.darekbx.flightssniffer.aircraft.AircraftInfo
import com.darekbx.flightssniffer.aircraft.AssetProvider
import com.darekbx.flightssniffer.repository.AirportsRepository
import com.darekbx.flightssniffer.repository.FlightsRepository
import com.darekbx.flightssniffer.repository.flightsinformation.getFlightsInformationService
import org.koin.dsl.module

object CommonModule {
    fun get() = module {
        single { AircraftInfo(get()) }
        single { AssetProvider((get() as Context).assets) }
        single { getFlightsInformationService() }
        single { FlightsRepository(get()) }
        single { AirportsRepository() }
    }
}
