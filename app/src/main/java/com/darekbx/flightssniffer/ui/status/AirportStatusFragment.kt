package com.darekbx.flightssniffer.ui.status

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.viewmodel.FlightsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AirportStatusFragment: Fragment(R.layout.fragment_airport_status) {

    private val flightsViewModel: FlightsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flightsList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        handleLoading(view)
        handleAirportInfo(view)
        loadFlights()

        view.findViewById<ImageView>(R.id.refresh_button).setOnClickListener { loadFlights() }
    }

    private fun handleAirportInfo(view: View) {
        flightsViewModel.activeAirport().observe(viewLifecycleOwner, { airport ->
            view.findViewById<TextView>(R.id.destination_airport).text = airport.name
        })
    }

    private fun handleLoading(view: View) {
        flightsViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            view.findViewById<FrameLayout>(R.id.loading_container)
                .visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun loadFlights() {
        flightsViewModel.loadFlights().observe(viewLifecycleOwner, { flights ->
            flightsList.adapter = FlightAdapter(flights)
        })
    }

    private val flightsList by lazy { requireView().findViewById<RecyclerView>(R.id.flights_list) }
}
