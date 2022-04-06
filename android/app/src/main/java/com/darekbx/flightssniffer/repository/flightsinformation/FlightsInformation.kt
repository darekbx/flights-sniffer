package com.darekbx.flightssniffer.repository.flightsinformation

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val FLIGHTRADAR24_LIVE_DATA_BASE_URL = "https://data-live.flightradar24.com"
private const val FLIGHTRADAR24_API_BASE_URL = "https://data-live.flightradar24.com"

private val service: FlightsInformation by lazy {

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(FLIGHTRADAR24_LIVE_DATA_BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttpClient)
        .build()

    retrofit.create(FlightsInformation::class.java)
}

fun getFlightsInformationService() = service

interface FlightsInformation {

    @GET("/zones/fcgi/feed.js")
    fun flights(@Query("bounds", encoded = true) bounds: String): Call<String>

    @GET("/clickhandler/")
    fun flightDetails(@Query("flight") flightId: String): Call<String>
}