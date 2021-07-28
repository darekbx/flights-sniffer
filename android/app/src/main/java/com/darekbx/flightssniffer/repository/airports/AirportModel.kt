package com.darekbx.flightssniffer.repository.airports

data class AirportModel(
    val iataCode: String,
    val countryCode: String,
    val name: String,
    val lat: Double,
    val lng: Double
)