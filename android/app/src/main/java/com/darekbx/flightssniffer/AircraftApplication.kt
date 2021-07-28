package com.darekbx.flightssniffer

import android.app.Application
import com.darekbx.flightssniffer.di.CommonModule
import com.darekbx.flightssniffer.di.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AircraftApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }
            androidContext(this@AircraftApplication)
            modules(CommonModule.get())
            modules(ViewModelModule.get())
        }
    }
}