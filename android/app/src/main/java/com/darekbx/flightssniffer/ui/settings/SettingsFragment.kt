package com.darekbx.flightssniffer.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.repository.airports.AirportModel
import com.darekbx.flightssniffer.ui.settings.boundsselector.BoundSelectActivity
import com.darekbx.flightssniffer.viewmodel.AirportsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.NumberFormatException

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
        return handlePreferenceClick(preference)
            ?: super.onPreferenceTreeClick(preference)
    }

    private fun handlePreferenceClick(preference: Preference?): Boolean? {
        return when {
            preference is SwitchPreferenceCompat -> handleTypesCheck(preference)
            preference?.key == ZONE_BOUNDARIES -> openBoundarySelect()
            else -> null
        }
    }

    private fun handleTypesCheck(preference: SwitchPreferenceCompat): Boolean {
        val trackDepartures = findPreference<SwitchPreferenceCompat>(TRACK_DEPARTURES)
        val trackArrivals = findPreference<SwitchPreferenceCompat>(TRACK_ARRIVALS)
        if (trackDepartures != null && trackArrivals != null && !preference.isChecked) {
            val departuresCheck = preference == trackDepartures && !trackArrivals.isChecked
            val arrivalsCheck = preference == trackArrivals && !trackDepartures.isChecked
            if (departuresCheck || arrivalsCheck) {
                preventFromTurnOff(preference)
                return true
            }
        }
        return false
    }

    private fun openBoundarySelect(): Boolean {
        startActivity(Intent(context, BoundSelectActivity::class.java))
        return true
    }

    private fun preventFromTurnOff(preference: SwitchPreferenceCompat): Boolean {
        showAtLeasOneTypeToast()
        preference.isChecked = true
        return true
    }

    private fun showAtLeasOneTypeToast() {
        Toast.makeText(context, R.string.preference_at_leas_one_toast, Toast.LENGTH_SHORT).show()
    }

    companion object {

        @Throws(NumberFormatException::class)
        fun String.toBounds(): DoubleArray = this
            .split(",")
            .map { it.trim().toDouble() }
            .toDoubleArray()

        const val TRACK_DEPARTURES = "trackDepartures"
        const val TRACK_ARRIVALS = "trackArrivals"
        const val AIRPORT_IATA = "airportIata"
        const val ZONE_BOUNDARIES = "zoneBoundaries"

        const val DEFAULT_BOUNDS = "57.00, 47.00, 12.00, 26.00"
        const val DEFAULT_AIRPORT_IATA = "WAW"
    }
}