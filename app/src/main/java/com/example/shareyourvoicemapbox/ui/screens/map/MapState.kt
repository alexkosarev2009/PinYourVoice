package com.example.shareyourvoicemapbox.ui.screens.map

import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

sealed interface MapState {
    data object NoConnection: MapState
    data class Content(
        val mapViewportState: MapViewportState = MapViewportState()
    ) : MapState
}