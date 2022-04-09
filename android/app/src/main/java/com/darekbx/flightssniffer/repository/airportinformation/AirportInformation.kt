package com.darekbx.flightssniffer.repository.airportinformation

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val FLIGHTRADAR24_API_BASE_URL = "https://api.flightradar24.com"

private val service: AirportInformation by lazy {

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(FLIGHTRADAR24_API_BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttpClient)
        .build()

    retrofit.create(AirportInformation::class.java)
}

fun getAirportInformationService() = service

interface AirportInformation {

    enum class Type(val typeName: String) {
        ARRIVALS("arrivals"),
        DEPARTURES("departures");

        override fun toString() = typeName
    }

    /**
     * @param timestamp in Seconds
     */
    @GET("/common/v1/airport.json")
    fun flights(
        @Query("code") airportCode: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("plugin-setting[schedule][timestamp]", encoded = true) timestamp: Long,
        @Query("plugin-setting[schedule][mode]", encoded = true) type: Type
    ): Call<String>
}