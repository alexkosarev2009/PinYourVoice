package com.example.shareyourvoicemapbox.ui.screens.map

import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

sealed interface MapState {
    data class Content(
        val markers: List<MarkerEntity> = emptyList(),
        val showAddMarkerDialog: Boolean = false,
        val showMicPermissionDialog: Boolean = false,
        val showFineLocationPermissionDialog: Boolean = false,
        val error: String = "",
        val isConnected: Boolean = true,
    ) : MapState
}