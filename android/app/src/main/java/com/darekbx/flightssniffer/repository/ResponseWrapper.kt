package com.darekbx.flightssniffer.repository

data class ResponseWrapper<T>(val response: T?, val errorMessage: String? = null)