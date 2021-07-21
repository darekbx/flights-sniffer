package com.darekbx.flightssniffer.repository

import com.darekbx.flightssniffer.repository.airports.AirportModel

class AirportsRepository {

    fun activeAirport() = AirportModel("WAW", "Warsaw Chopin Airport", 52.1672369, 20.9678911)
}
