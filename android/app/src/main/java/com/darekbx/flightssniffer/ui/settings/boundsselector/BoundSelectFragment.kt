package com.darekbx.flightssniffer.ui.settings.boundsselector

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.darekbx.flightssniffer.BuildConfig
import com.darekbx.flightssniffer.R
import com.darekbx.flightssniffer.ui.settings.SettingsFragment
import com.darekbx.flightssniffer.ui.settings.SettingsFragment.Companion.toBounds
import org.koin.android.ext.android.inject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polygon
import java.lang.NumberFormatException

class BoundSelectFragment : Fragment(R.layout.fragment_bound_select) {

    private val sharedPreferences: SharedPreferences by inject()
    private var savedBoundDisplayed = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeMap()

        buttonSave.setOnClickListener { save() }
        buttonReset.setOnClickListener { reset() }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun initializeMap() {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.controller.setZoom(5.0)
        map.setMultiTouchControls(true)

        map.overlays.add(touchOverlay)

        drawBounds(
            GeoPoint(flightBounds[0], flightBounds[2]),
            GeoPoint(flightBounds[1], flightBounds[3])
        )

        map.controller.setCenter(
            GeoPoint(
                flightBounds[0] - flightBounds[2],
                flightBounds[1] - flightBounds[3]
            )
        )
    }

    private fun save() {
        firstPoint?.let { firstPoint ->
            secondPoint?.let { secondPoint ->
                val bounds = doubleArrayOf(
                    firstPoint.latitude,
                    secondPoint.latitude,
                    firstPoint.longitude,
                    secondPoint.longitude
                )
                sharedPreferences
                    .edit()
                    .putString(SettingsFragment.ZONE_BOUNDARIES, bounds.joinToString(","))
                    .apply()
                requireActivity().finish()
            } ?: boundsNotSetToast()
        } ?: boundsNotSetToast()
    }

    private fun boundsNotSetToast() {
        Toast.makeText(context, R.string.bounds_not_set, Toast.LENGTH_SHORT).show()
    }

    private fun reset() {
        firstPoint = null
        secondPoint = null
        map.overlays.clear()
        map.overlays.add(touchOverlay)
        map.invalidate()
    }

    private fun drawBounds() {
        firstPoint?.let { firstPoint ->
            addMarker(firstPoint)
            secondPoint?.let { secondPoint ->
                addMarker(secondPoint)
                drawBounds(firstPoint, secondPoint)
            }
        }
        map.invalidate()
    }

    private fun drawBounds(
        firstPoint: GeoPoint,
        secondPoint: GeoPoint
    ) {
        val bounds = Polygon(map).apply {
            actualPoints.addAll(
                listOf(
                    firstPoint,
                    GeoPoint(firstPoint.latitude, secondPoint.longitude),
                    secondPoint,
                    GeoPoint(secondPoint.latitude, firstPoint.longitude)
                )
            )
            fillPaint.color = Color.argb(60, 255, 0, 0)
            fillPaint.strokeWidth = 1.0F
        }
        map.overlays.add(bounds)
    }

    private fun addMarker(point: GeoPoint) {
        val firstPointMarker = Marker(map).apply {
            position = point
            setAnchor(0.5F, 0.5F)
            icon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_circle,
                requireActivity().theme
            )
        }
        map.overlays.add(firstPointMarker)
    }

    private val touchOverlay = object : Overlay() {

        override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
            if (savedBoundDisplayed) {
                savedBoundDisplayed = false
                reset()
            }

            if (e != null && mapView != null && secondPoint == null) {
                val location = mapView.projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                when {
                    firstPoint == null -> firstPoint = location
                    secondPoint == null -> secondPoint = location
                }
                drawBounds()
            }
            return super.onSingleTapConfirmed(e, mapView)
        }
    }

    private val flightBounds by lazy {
        val bounds = sharedPreferences.getString(SettingsFragment.ZONE_BOUNDARIES, null)
            ?: SettingsFragment.DEFAULT_BOUNDS
        try {
            bounds.toBounds()
        } catch (e: NumberFormatException) {
            SettingsFragment.DEFAULT_BOUNDS.toBounds()
        }
    }

    private val map: MapView by lazy { requireView().findViewById(R.id.map) }
    private val buttonReset: ImageView by lazy { requireView().findViewById(R.id.reset_button) }
    private val buttonSave: ImageView by lazy { requireView().findViewById(R.id.save_button) }

    private var firstPoint: GeoPoint? = null
    private var secondPoint: GeoPoint? = null
}