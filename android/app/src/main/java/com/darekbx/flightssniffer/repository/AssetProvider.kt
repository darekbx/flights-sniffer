package com.darekbx.flightssniffer.repository

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException

class AssetProvider(
    private val assetManager: AssetManager
) {
    private var cachedSprite: Bitmap? = null

    fun loadBigAircraft(): String?  = loadAsset(BIG_AIRCRAFT_FILE)

    fun loadAircraftNotifications(): String?  = loadAsset(AIRCRAFT_NOTIFICATIONS_FILE)

    fun loadAircraftInfo(): String? = loadAsset(AIRCRAFT_FILE)

    fun loadAirports(): String? = loadAsset(AIRPORTS_FILE)

    fun loadAircraftIconsInfo(): String? = loadAsset(ICONS_FILE)

    fun loadAircraftIconsSprite(): Bitmap? {
        if (cachedSprite != null) {
            return cachedSprite
        }
        try {
            assetManager.open(ASSET_SPRITE_FILE).use {
                return BitmapFactory.decodeStream(it).also { bitmap ->
                    cachedSprite = bitmap
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to read icons sprite asset", e)
            return null
        }
    }

    private fun loadAsset(file: String): String? {
        try {
            assetManager.open(file).use {
                return String(it.readBytes())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to read $file file", e)
            return null
        }
    }

    companion object {
        private const val TAG = "AssetProvider"
        private const val ICONS_FILE = "aircraft_frames.json"
        private const val ASSET_SPRITE_FILE = "aircraft_sprite.png"
        private const val AIRPORTS_FILE = "airport_codes.json"
        private const val AIRCRAFT_FILE = "aircraft_dictionary.json"
        private const val BIG_AIRCRAFT_FILE = "big_aircraft.json"
        private const val AIRCRAFT_NOTIFICATIONS_FILE = "aircraft_notifications.json"
    }
}