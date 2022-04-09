package com.darekbx.flightssniffer.repository.airportinformation

class Wrapper(val result: Result)

class Result(val response: Response)

class Response(val airport: Airport)

class Airport(val pluginData: PluginData)

class PluginData(val schedule: Schedule)

class Schedule(val arrivals: ScheduleResponse, val departures: ScheduleResponse)

class ScheduleResponse(val item: Page, val page: Page, val data: Array<DataWrapper>)

class DataWrapper(val flight: Flight)

class Page(val current: Int, val total: Int)

data class Flight(val aircraft: Aircraft?, val status: Status) {
    var airportType: AirportInformation.Type? = null
}

class Status(val text: String)

class Aircraft(val model: AicraftModel?)

class AicraftModel(val code: String, val text: String)