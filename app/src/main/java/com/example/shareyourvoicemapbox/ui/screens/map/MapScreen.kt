package com.example.shareyourvoicemapbox.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel<MapViewModel>()
) {
    val state by viewModel.uiState.collectAsState()
    when (state) {
        MapState.NoConnection -> {

        }

        else -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.BottomEnd
            ) {
                MapboxMap(
                    Modifier.fillMaxSize(),
                    mapViewportState = state.mapViewportState,
                    scaleBar = {

                    },
                    logo = {
                        Logo()
                    }
                )
            }
        }
    }
}