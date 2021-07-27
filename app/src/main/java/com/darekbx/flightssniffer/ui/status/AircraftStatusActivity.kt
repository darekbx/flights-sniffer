package com.darekbx.flightssniffer.ui.status

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.darekbx.flightssniffer.R

class AircraftStatusActivity : AppCompatActivity(R.layout.activity_fragment_container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.commit {
            add(R.id.fragment_container, AirportStatusFragment(), "AirportStatusFragment")
        }
    }
}