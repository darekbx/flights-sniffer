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

    fun loadAircraftInfo(): String? {
        try {
            assetManager.open(AIRCRAFT_FILE).use {
                return String(it.readBytes())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to read aicraft json asset", e)
            return null
        }
    }

    fun loadAirports(): String? {
        try {
            assetManager.open(AIRPORTS_FILE).use {
                return String(it.readBytes())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to read airports json asset", e)
            return null
        }
    }

    fun loadAircraftIconsInfo(): String? {
        try {
            assetManager.open(ASSET_JSON_FILE).use {
                return String(it.readBytes())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to read icons json asset", e)
            return null
        }
    }

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

    companion object {
        private const val TAG = "AssetProvider"
        private const val ASSET_JSON_FILE = "aircraft_frames.json"
        private const val ASSET_SPRITE_FILE = "aircraft_sprite.png"
        private const val AIRPORTS_FILE = "airport_codes.json"
        private const val AIRCRAFT_FILE = "aircraft_dictionary.json"
    }
}