package com.darekbx.flightssniffer.ui.flightdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.darekbx.flightssniffer.R

class FlightDetailsActivity : AppCompatActivity(R.layout.activity_fragment_container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.commit {
            add(
                R.id.fragment_container,
                FlightDetailsFragment::class.java,
                intent.extras,
                "FlightDetailsFragment"
            )
        }

        intent.getStringExtra(FlightDetailsFragment.CALL_SIGN_KEY)?.let { callSign->
            setTitle(getString(R.string.details, callSign))
        }
    }
}