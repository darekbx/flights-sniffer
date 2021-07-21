package com.darekbx.flightssniffer.di

import com.darekbx.flightssniffer.viewmodel.FlightsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    fun get() = module {
        viewModel { FlightsViewModel(get(), get(), get()) }
    }
}
