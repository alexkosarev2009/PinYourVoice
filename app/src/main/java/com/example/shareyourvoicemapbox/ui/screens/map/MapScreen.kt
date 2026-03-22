package com.example.shareyourvoicemapbox.ui.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.extension.compose.MapboxMap
import kotlinx.coroutines.delay

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

    when (state) {
        MapState.NoConnection -> {

        }

        else -> {
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
                        scaleBar = {

                        },
                        logo = {
                            Logo()
                        }
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
    }
}