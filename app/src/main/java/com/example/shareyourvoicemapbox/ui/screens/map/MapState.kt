package com.example.shareyourvoicemapbox.ui.screens.map

import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

sealed interface MapState {
    data object NoConnection: MapState
    data class Content(
        val markers: List<MarkerEntity> = emptyList(),
        val mapViewportState: MapViewportState = MapViewportState(),
        val isRecording: Boolean = false,
        val showAddMarkerDialog: Boolean = false,
        val showMicPermissionDialog: Boolean = false,
        val error: String = ""
    ) : MapState
}