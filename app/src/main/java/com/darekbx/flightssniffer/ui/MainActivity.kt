package com.darekbx.flightssniffer.ui

import androidx.appcompat.app.AppCompatActivity
import com.darekbx.flightssniffer.R

/*

Main Screen:
 - is displaying all arrivals or departures from/to defined airport from selected bounds
 - on the top is displayed airport plane icon with number of the planes approaching/arriving
   - There's {13} planes {approaching/arriving} {to/from} {WAW} airport
 - below is the list of the planes (ordered by arrival/departire time asc)
 - list items:
    - time, from airport, flight code, plane details, destination
    - click goes into details
 - settings:
    - boolean: departures or arrivals
    - stringSet: select bounds on the map
    - string: select airport with it's lat/lng (by its IATA code)

    - stringSet: big planes (define from autocomplete (AircraftInfo.allNames())

 */

class MainActivity : AppCompatActivity(R.layout.activity_main)
