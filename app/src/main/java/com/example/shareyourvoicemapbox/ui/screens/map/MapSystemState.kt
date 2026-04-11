package com.example.shareyourvoicemapbox.ui.screens.map

import com.mapbox.geojson.Point

data class MapSystemState(
    val isRecording: Boolean = false,
    val recordTimeMs: Long = 0L,
    val currentAudioPath: String? = null,

    val hasLocationPermission: Boolean = false,
    val userLocation: Point? = null

)