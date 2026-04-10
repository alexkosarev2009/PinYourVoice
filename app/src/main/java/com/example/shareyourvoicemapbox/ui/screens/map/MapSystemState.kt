package com.example.shareyourvoicemapbox.ui.screens.map

import com.mapbox.geojson.Point

data class MapSystemState(
    val isRecording: Boolean = false,
    val recordingProgress: Float = 0f,
    val currentAudioPath: String = "",

    val hasLocationPermission: Boolean = false,
    val userLocation: Point? = null
)