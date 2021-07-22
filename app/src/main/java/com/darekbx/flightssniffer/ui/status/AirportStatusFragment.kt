package com.darekbx.flightssniffer.ui.status

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
        handleError(view)
        loadStatus()

        view.findViewById<ImageView>(R.id.refresh_button).setOnClickListener { loadStatus() }
    }

    private fun handleLoading(view: View) {
        flightsViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            view.findViewById<FrameLayout>(R.id.loading_container)
                .visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun handleError(view: View) {
        flightsViewModel.errorMessage.observe(viewLifecycleOwner, { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        })
    }

    private fun loadStatus() {
        with(flightsViewModel) {
            flights.observe(viewLifecycleOwner, { flights ->
                flightsList.adapter = FlightAdapter(flights)
            })
            activeAirport.observe(viewLifecycleOwner, { airport ->
                requireView().findViewById<TextView>(R.id.destination_airport).text =
                    "${airport.name} (${airport.countryCode})"
            })
            loadStatus()
        }
    }

    private val flightsList by lazy { requireView().findViewById<RecyclerView>(R.id.flights_list) }
}