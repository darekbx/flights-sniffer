package com.darekbx.flightssniffer.ui.status

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darekbx.flightssniffer.BuildConfig
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.repository.flightsinformation.Flight
import com.darekbx.flightssniffer.viewmodel.FlightsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class AirportStatusFragment: Fragment(R.layout.fragment_airport_status) {

    private val flightsViewModel: FlightsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        flightsList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        flightsList.adapter = flightsAdapter

        handleLoading(view)
        handleError()
        handleStatus()
        initializeMap()
        setCenterOfTheMap()

        flightsViewModel.loadStatus()

        view.findViewById<ImageView>(R.id.refresh_button).setOnClickListener {
            flightsViewModel.loadStatus()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {

                findNavController().navigate(R.id.action_airportStatusFragment_to_settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun setCenterOfTheMap() {
        flightsViewModel.activeAirport.observe(viewLifecycleOwner, { airport ->
            map.controller.setCenter(GeoPoint(airport.lat, airport.lng))
        })
    }

    private fun addFlightsToMap(flights: List<Flight>) {
        map.overlays.clear()

        for (flight in flights) {
            val marker = Marker(map).apply {
                icon = BitmapDrawable(resources, flight.icon)
                position = GeoPoint(flight.lat, flight.lng)
                rotation = flight.mapRotation
            }
            map.overlays.add(marker)
        }

        map.invalidate()
    }

    private fun handleLoading(view: View) {
        flightsViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            view.findViewById<FrameLayout>(R.id.loading_container)
                .visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun handleError() {
        flightsViewModel.errorMessage.observe(viewLifecycleOwner, { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        })
    }

    private fun handleStatus() {
        with(flightsViewModel) {
            flights.observe(viewLifecycleOwner, { flights ->
                flightsAdapter.invalidate(flights)
                addFlightsToMap(flights)
            })
            activeAirport.observe(viewLifecycleOwner, { airport ->
                requireView().findViewById<TextView>(R.id.destination_airport).text =
                    "${airport.name} (${airport.countryCode})"
            })
        }
    }

    private val flightsAdapter by lazy { FlightAdapter() }
    private val flightsList by lazy { requireView().findViewById<RecyclerView>(R.id.flights_list) }
    private val map by lazy { requireView().findViewById<MapView>(R.id.map) }

    companion object {
        private const val INITIAL_ZOOM = 8.0
    }
}