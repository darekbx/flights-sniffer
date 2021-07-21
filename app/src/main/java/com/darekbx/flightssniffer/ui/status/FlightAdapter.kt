package com.darekbx.flightssniffer.ui.status

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.repository.flightsinformation.Flight

class FlightAdapter(private val data: List<Flight>):
    RecyclerView.Adapter<FlightAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView
        val icao: TextView
        val speed: TextView
        val altitude: TextView
        val flight: TextView
        val flightInfo: TextView
        val distanceLeft: TextView

        init {
            icon = view.findViewById(R.id.icon)
            icao = view.findViewById(R.id.icao)
            speed = view.findViewById(R.id.speed)
            altitude = view.findViewById(R.id.altitude)
            flight = view.findViewById(R.id.flight)
            flightInfo = view.findViewById(R.id.flight_info)
            distanceLeft = view.findViewById(R.id.distance_left)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_flight, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.icao.context
        with(data[position]) {
            viewHolder.icon.setImageBitmap(icon)
            viewHolder.icao.text = icao
            viewHolder.speed.text = context.getString(R.string.speed, speedInKmH)
            viewHolder.altitude.text = context.getString(R.string.altitude, altitudeInMeters)
            viewHolder.flight.text = flight
            viewHolder.flightInfo.text = context.getString(R.string.flight_info, origin, destination)
            viewHolder.distanceLeft.text = context.getString(R.string.distance_left, distanceLeft)
        }
    }

    override fun getItemCount() = data.size
}
