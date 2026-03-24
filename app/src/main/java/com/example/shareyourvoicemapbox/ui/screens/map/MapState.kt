package com.example.shareyourvoicemapbox.ui.screens.map

import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

sealed interface MapState {
    data object NoConnection: MapState
    data class Error(
        val message: String
    ) : MapState
    data class Content(
        val markers: List<MarkerEntity> = emptyList(),
        val mapViewportState: MapViewportState = MapViewportState()
    ) : MapState
}