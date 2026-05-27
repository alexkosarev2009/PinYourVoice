package com.example.shareyourvoicemapbox.ui.screens.map

import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

data class MapSystemState(
    val isRecording: Boolean = false,
    val recordTimeMs: Long = 0L,
    val currentRecordAudioPath: String? = null,

    val isPlaying: Boolean = false,
    val currentPlayAudioPath: String? = null,

    val isRecordingSaved: Boolean = false,

    val hasLocationPermission: Boolean = false,
    val userLocation: Point? = null,
    val hasCenteredUser: Boolean = false,
    val mapViewportState: MapViewportState = MapViewportState(),

    val isPublicSelected: Boolean = true,
    )