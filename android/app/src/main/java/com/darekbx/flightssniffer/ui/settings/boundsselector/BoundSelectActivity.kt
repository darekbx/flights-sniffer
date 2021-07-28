package com.darekbx.flightssniffer.ui.settings.boundsselector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.darekbx.flightssniffer.R

class BoundSelectActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        supportFragmentManager.commit {
            add(R.id.fragment_container, BoundSelectFragment(), "TAG")
        }

        setTitle(R.string.bounds_select)
    }
}