package com.darekbx.flightssniffer.aircraft

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException

class AssetProvider(
    private val assetManager: AssetManager
) {
    private var cachedSprite: Bitmap? = null

    fun loadJson(): String? {
        try {
            assetManager.open(ASSET_JSON_FILE).use {
                return String(it.readBytes())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to read json asset", e)
            return null
        }
    }

    fun loadImage(): Bitmap? {
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
            Log.e(TAG, "Unable to read bitmap asset", e)
            return null
        }
    }

    companion object {
        private const val TAG = "AssetProvider"
        private const val ASSET_JSON_FILE = "aircraft_frames.json"
        private const val ASSET_SPRITE_FILE = "aircraft_sprite.png"
    }
}
