package com.darekbx.flightssniffer.repository.aircraft

import android.graphics.Bitmap
import android.util.Log
import com.darekbx.flightssniffer.repository.AssetProvider
import org.json.JSONException
import org.json.JSONObject

class AircraftIcons(
    private val assetProvider: AssetProvider
) {

    private val cachedHolders = mutableListOf<IconHolder>()

    fun loadAircraftIcon(name: String): Bitmap? {
        val assetJson = assetProvider.loadAircraftIconsInfo()
        val assetSprite = assetProvider.loadAircraftIconsSprite()
        if (assetJson != null && assetSprite != null) {
            val iconHolders = parseAssetWithCache(assetJson)
            val iconHolder = iconHolders.firstOrNull { it.names.contains(name) }
            if (iconHolder != null) {
                return cutFrame(assetSprite, iconHolder)
            }
        }
        return null
    }

    fun loadNames(): List<String> {
        val assetJson = assetProvider.loadAircraftIconsInfo()
        if (assetJson != null) {
            val iconHolders = parseAssetWithCache(assetJson)
            return iconHolders.flatMap { it.names }
        }
        return emptyList()
    }

    private fun parseAssetWithCache(assetJson: String): List<IconHolder> {
        if (cachedHolders.isNotEmpty()) {
            return cachedHolders
        }
        val iconHolders = parseAssets(assetJson)
        cachedHolders.addAll(iconHolders)
        return iconHolders
    }

    private fun parseAssets(assetJson: String): MutableList<IconHolder> {
        val iconHolders = mutableListOf<IconHolder>()
        try {
            val jsonObject = JSONObject(assetJson)
            val icons = jsonObject.getJSONObject("icons")

            icons.keys().forEach { key ->
                val names = mutableListOf(key)

                val nameObject = icons.getJSONObject(key)
                val aliases = nameObject.getJSONArray("aliases")

                for (aliasIndex in (0 until aliases.length())) {
                    names.add(aliases.getString(aliasIndex))
                }

                val frames = nameObject.getJSONArray("frames")
                val frame = frames.getJSONObject(0)

                val rotationFrame = frame.optJSONObject("$ROTATION_ANGLE")
                    ?: frame.getJSONObject("0")

                val x = rotationFrame.getInt("x")
                val y = rotationFrame.getInt("y")
                val w = rotationFrame.getInt("w")
                val h = rotationFrame.getInt("h")

                val iconHolder = IconHolder(names, x, y, w, h)
                iconHolders.add(iconHolder)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Unable to parse asset json", e)
        }
        return iconHolders
    }

    private fun cutFrame(sprite: Bitmap, iconHolder: IconHolder): Bitmap {
        with(iconHolder) {
            val iconBitmap = Bitmap.createBitmap(sprite, x, y, width, height)
            return Bitmap.createScaledBitmap(
                iconBitmap,
                (width * ICON_SCALE_FACTOR).toInt(),
                (height * ICON_SCALE_FACTOR).toInt(),
                false
            )
        }
    }

    companion object {
        private const val TAG = "AircraftIcon"
        private const val ICON_SCALE_FACTOR = 2.0
        private const val ROTATION_ANGLE = 45
    }
}