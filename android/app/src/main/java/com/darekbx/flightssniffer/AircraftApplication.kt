package com.darekbx.flightssniffer

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.util.Log
import com.darekbx.flightssniffer.di.CommonModule
import com.darekbx.flightssniffer.di.ViewModelModule
import com.darekbx.flightssniffer.service.BackgroundAirplaneCheckJobService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

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

        scheduleJob()
    }

    fun scheduleJob() {
        val componentName = ComponentName(this, BackgroundAirplaneCheckJobService::class.java)
        val info = JobInfo.Builder(123, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setPeriodic(TimeUnit.HOURS.toMillis(1))
            .build()
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("AircraftStatusActivity", "Job scheduled")
        } else {
            Log.d("AircraftStatusActivity", "Job scheduling failed")
        }
    }
}