package com.darekbx.flightssniffer.ui.status

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.repository.flightsinformation.Flight

class FlightAdapter:
    RecyclerView.Adapter<FlightAdapter.ViewHolder>() {

    private var data = listOf<Flight>()

    var onItemClicked: (Flight) -> Unit = { }

    fun invalidate(data: List<Flight>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
        val row: View = view.findViewById(R.id.row)
        val icon: ImageView = view.findViewById(R.id.icon)
        val bigPlaneMark: ImageView = view.findViewById(R.id.big_plane_mark)
        val aircraft: TextView = view.findViewById(R.id.aircraft)
        val speed: TextView = view.findViewById(R.id.speed)
        val altitude: TextView = view.findViewById(R.id.altitude)
        val flight: TextView = view.findViewById(R.id.flight)
        val flightInfo: TextView = view.findViewById(R.id.flight_info)
        val distanceLeft: TextView = view.findViewById(R.id.distance_left)

        init {
            row.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.adapter_flight, viewGroup, false)
        return ViewHolder(view) { position -> onItemClicked(data[position]) }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val context = viewHolder.aircraft.context
        with(data[position]) {
            loadIcon(viewHolder)
            setBigPlaneMark(viewHolder)
            viewHolder.aircraft.text = aircraftName ?: icao
            viewHolder.speed.text = context.getString(R.string.speed, speedInKmH)
            viewHolder.altitude.text = context.getString(R.string.altitude, altitudeInMeters)
            viewHolder.flight.text = flight
            viewHolder.flightInfo.text =
                context.getString(R.string.flight_info, origin, destination)
            viewHolder.distanceLeft.text = context.getString(R.string.distance_left, distanceLeft)
        }
    }
    private fun Flight.setBigPlaneMark(viewHolder: ViewHolder) {
        viewHolder.bigPlaneMark.visibility = if (isBigPlane) View.VISIBLE else View.GONE
    }

    private fun Flight.loadIcon(viewHolder: ViewHolder) {
        with(viewHolder.icon) {
            if (icon == null) {
                setImageResource(R.drawable.ic_image_not_supported)
            } else {
                setImageBitmap(icon)
            }
        }
    }

    override fun getItemCount() = data.size
}