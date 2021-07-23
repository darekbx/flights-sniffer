package com.darekbx.flightssniffer.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.repository.airports.AirportModel
import com.darekbx.flightssniffer.viewmodel.AirportsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private val airportsViewModel: AirportsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        airportsViewModel.loadAirports().observe(viewLifecycleOwner, { airports ->
            verifyIataCode(airports)
        })
    }

    private fun verifyIataCode(airports: List<AirportModel>) {
        val airportIataPreference = findPreference<EditTextPreference>(AIRPORT_IATA)
        airportIataPreference?.setOnPreferenceChangeListener { _, newValue ->
            val isValidCode = airports.any { it.iataCode == newValue }
            return@setOnPreferenceChangeListener if (!isValidCode) {
                displayInvalidIataCodeToast()
                false
            } else {
                true
            }
        }
    }

    private fun displayInvalidIataCodeToast() {
        Toast.makeText(
            context,
            R.string.preference_airport_iata_invalid,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        handleTrackTypeClick(preference)
        return super.onPreferenceTreeClick(preference)
    }

    private fun handleTrackTypeClick(preference: Preference?) {
        val trackDepartures = findPreference<SwitchPreferenceCompat>(TRACK_DEPARTURES)
        val trackArrivals = findPreference<SwitchPreferenceCompat>(TRACK_ARRIVALS)
        if (trackDepartures != null && trackArrivals != null) {
            if (preference == trackDepartures) {
                trackArrivals.isChecked = !trackDepartures.isChecked
            }
            if (preference == trackArrivals) {
                trackDepartures.isChecked = !trackArrivals.isChecked
            }
        }
    }

    companion object {
        val TRACK_DEPARTURES = "trackDepartures"
        val TRACK_ARRIVALS = "trackArrivals"
        val AIRPORT_IATA = "airportIata"
        val ZONE_BOUNDARIES = "zoneBoundaries"

        val DEFAULT_BOUNDS = "57.00, 47.00, 12.00, 26.00"
        const val DEFAULT_AIRPORT_IATA = "WAW"
    }
}