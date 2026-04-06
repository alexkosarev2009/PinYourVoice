package com.example.shareyourvoicemapbox.ui.screens.map

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.Marker
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel<MapViewModel>()
) {
    val state by viewModel.uiState.collectAsState()

    var overlayVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(300)
        overlayVisible = false
    }

    when (val currentState = state) {
        is MapState.NoConnection -> {
            Text("No connection")
        }

        is MapState.Error -> {
            MapError(
                modifier = modifier,
                state = currentState,
                overlayVisible = overlayVisible
            )
        }

        is MapState.Content -> {
            MapContent(
                modifier = modifier,
                state = currentState,
                overlayVisible = overlayVisible,
                onMarkerClick = {

                }
            )
        }
    }
}

@Composable
fun MapContent(
    modifier: Modifier = Modifier,
    state: MapState.Content,
    overlayVisible: Boolean,
    onMarkerClick: (MarkerEntity) -> Unit,
    showBottomSheet: Boolean = false,
) {
    val context = LocalContext.current
    val overlayVisible = overlayVisible

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(1000))
        ) {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = state.mapViewportState,
                scaleBar = {},
                logo = {
                    Logo()
                }
            ) {
                val markerIcon = rememberIconImage(key = "red-marker", painter =
                    painterResource(id = R.drawable.red_marker))
                state.markers.forEach { marker ->
                    PointAnnotation(
                        point = Point.fromLngLat(marker.lng, marker.lat)
                    ) {
                        iconImage = markerIcon
                        interactionsState.onClicked {
                            onMarkerClick(marker)
                            true
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = overlayVisible,
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFF051728))
            )
        }

    }
}

@Composable
fun MapError(
    modifier: Modifier = Modifier,
    state: MapState.Error,
    overlayVisible: Boolean
) {
    val overlayVisible = overlayVisible

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(1000))
        ) {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = state.mapViewportState,
                scaleBar = {}
            )
        }
        AnimatedVisibility(
            visible = overlayVisible,
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFF051728))
            )
        }
    }
}