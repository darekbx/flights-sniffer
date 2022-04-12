package com.darekbx.flightssniffer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.repository.airportinformation.AirportInformation
import com.darekbx.flightssniffer.repository.airportinformation.AirportInformationRepository
import com.darekbx.flightssniffer.ui.status.AircraftStatusActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.Exception

class BackgroundAirplaneCheckJobService: JobService(), KoinComponent {

    private val airportInformationRepository : AirportInformationRepository by inject()
    private val sharedPreferences : SharedPreferences by inject()

    override fun onStartJob(params: JobParameters?): Boolean {
        loadArrivals(params)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    private fun loadArrivals(params: JobParameters?) {
        if (!sharedPreferences.getBoolean("bigAircraftNotifications", true)) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val foundData = airportInformationRepository.searchForBigAircraft()
                if (foundData.isNotEmpty()) {
                    val uniqueAircraft = foundData.distinctBy { it.aircraft?.model?.code }
                    uniqueAircraft.forEachIndexed { index, flight ->
                        val message = when (flight.airportType) {
                            AirportInformation.Type.ARRIVALS ->
                                "${flight.aircraft?.model?.text} will arrive at ${flight.status.text}"
                            AirportInformation.Type.DEPARTURES ->
                                "${flight.aircraft?.model?.text} will be departure at ${flight.status.text}"
                            else -> "${flight.aircraft?.model?.text} will departure/arrive at ${flight.status.text}"
                        }
                        val notification = createNotification(message)
                        notificationManager.notify(index, notification)
                    }
                }
                Log.v(TAG, "Got ${foundData.size} notifications")
                jobFinished(params, false)
            } catch (e: Exception) {
                e.printStackTrace()
                jobFinished(params, true)
            }
        }
    }

    private fun createNotification(text: String): Notification {
        val tracksIntent = Intent(applicationContext, AircraftStatusActivity::class.java)
        val tracksPendingIntent = PendingIntent.getActivity(
            applicationContext, 0,
            tracksIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_radar)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setContentIntent(tracksPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        var channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
        if (channel == null) {
            channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return builder.build()
    }

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    companion object {
        private const val TAG = "BackgroundAirplaneCheckJobService"
        private const val NOTIFICATION_CHANNEL_ID = "flight_radar_channel_id"
    }
}