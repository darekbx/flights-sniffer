package com.darekbx.flightssniffer.ui.flightdetails

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.darekbx.flightssniffer.BuildConfig
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.repository.flightsinformation.FlightDetails
import com.darekbx.flightssniffer.viewmodel.FlightsViewModel
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class FlightDetailsFragment: Fragment(R.layout.fragment_flight_details) {

    private val flightsViewModel: FlightsViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flightId()?.let { flightId ->
            flightsViewModel.loadDetails(flightId)
        }

        handleLoading()
        handleError()
        handleDetails()
        initializeMap()
        setCenterOfTheMap()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        flightsViewModel.loadStatus()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun flightId() = arguments?.getString(FLIGHT_ID_KEY)

    private fun handleDetails() {
        flightsViewModel.flightDetails.observe(viewLifecycleOwner, { flightDetails ->
            displayDetails(flightDetails)
        })
    }

    private fun displayDetails(flightDetails: FlightDetails) {
        planeInfo.text = flightDetails.aircraft.model
        airlineInfo.text = flightDetails.airline
        travelInfo.text = getString(
            R.string.flight_info,
            flightDetails.origin.iata,
            flightDetails.destination.iata
        )
        travelInfoDetailed.text = getString(
            R.string.flight_info,
            flightDetails.origin.name,
            flightDetails.destination.name
        )

        displayEstimated(flightDetails)
        displayTrail(flightDetails)

        Picasso.get().load(flightDetails.aircraft.imageUrl).into(planeImage)
    }

    private fun displayEstimated(flightDetails: FlightDetails) {
        flightEstimated.text = flightDetails.estimatedText
        val statusColorId = STATUS_COLORS[flightDetails.statusColor]
        if (statusColorId != null) {
            flightEstimated.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    statusColorId,
                    context?.theme
                )
            )
        }
    }

    private fun displayTrail(flightDetails: FlightDetails) {
        map.overlays.clear()

        val points = flightDetails.trail.map { GeoPoint(it.lat, it.lng) }
        val polyline = Polyline().apply {
            outlinePaint.color = Color.RED
            outlinePaint.strokeWidth = 2.0F
            setPoints(points)
        }
        map.overlays.add(polyline)

        flightsViewModel.flights.value
            ?.firstOrNull { it.flightId == flightId() }
            ?.let { flight ->
                val icon = Marker(map).apply {
                    icon = BitmapDrawable(resources, flight.icon)
                    position = GeoPoint(flight.lat, flight.lng)
                    setAnchor(0.5F, 0.5F)
                    rotation = flight.mapRotation
                }
                map.overlays.add(icon)
            }

        map.invalidate()
    }

    private fun setCenterOfTheMap() {
        flightsViewModel.activeAirport.observe(viewLifecycleOwner, { airport ->
            map.controller.setCenter(GeoPoint(airport.lat, airport.lng))
        })
    }

    private fun initializeMap() {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.controller.setZoom(INITIAL_ZOOM)
        map.setMultiTouchControls(true)
    }

    private fun handleLoading() {
        flightsViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            requireView().findViewById<FrameLayout>(R.id.loading_container)
                .visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun handleError() {
        flightsViewModel.errorMessage.observe(viewLifecycleOwner, { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        })
    }

    private val planeInfo: TextView by lazy { requireView().findViewById(R.id.plane_info) }
    private val airlineInfo: TextView by lazy { requireView().findViewById(R.id.airline_info) }
    private val planeImage: ImageView by lazy { requireView().findViewById(R.id.plane_image) }
    private val travelInfo: TextView by lazy { requireView().findViewById(R.id.travel_info) }
    private val travelInfoDetailed: TextView by lazy { requireView().findViewById(R.id.travel_info_detailed) }
    private val flightEstimated: TextView by lazy { requireView().findViewById(R.id.flight_estimated) }
    private val map: MapView by lazy { requireView().findViewById(R.id.map) }

    companion object {
        const val FLIGHT_ID_KEY = "FLIGHT_ID"
        private val INITIAL_ZOOM = 6.0

        private val STATUS_COLORS = mapOf(
            "grey" to R.color.grey,
            "green" to R.color.green,
            "yellow" to R.color.yellow,
            "orange" to R.color.orange,
            "red" to R.color.red
        )
    }
}