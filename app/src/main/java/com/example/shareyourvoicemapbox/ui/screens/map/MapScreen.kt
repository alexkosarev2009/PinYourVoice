package com.example.shareyourvoicemapbox.ui.screens.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel
) {
    val state by viewModel.uiState.collectAsState()

    var overlayVisible by remember { mutableStateOf(true) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {

        }
    }
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        delay(300)
        overlayVisible = false
    }

    when (val currentState = state) {
        is MapState.NoConnection -> {
            Text("No connection")
        }


        is MapState.Content -> {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            when {
                                permissionState.status.isGranted -> {
                                    viewModel.openAddMarkerDialog()
                                }
                                permissionState.status.shouldShowRationale -> {
                                    viewModel.openPermissionSettingsDialog()
                                }
                                else -> {
                                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddLocationAlt,
                            contentDescription = "Add marker",
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }
            ) {
                MapContent(
                    modifier = modifier,
                    state = currentState,
                    overlayVisible = overlayVisible,
                    onMarkerClick = {

                    },
                    onAddMarkerDismiss = {
                        viewModel.closeAddMarkerDialog()
                    },
                    onConfirmPermissionSettingsDialog = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        viewModel.closePermissionSettingsDialog()
                    },
                    onDismissPermissionSettingsDialog = {
                        viewModel.closePermissionSettingsDialog()
                    },
                    onRecordClick = {
                        viewModel.startRecording()
                    }
                )

            }

        }
    }
}

@Composable
fun MapContent(
    modifier: Modifier = Modifier,
    state: MapState.Content,
    overlayVisible: Boolean,
    onMarkerClick: (MarkerEntity) -> Unit,
    onAddMarkerDismiss: () -> Unit,
    onConfirmPermissionSettingsDialog: () -> Unit,
    onDismissPermissionSettingsDialog: () -> Unit,
    onRecordClick: () -> Unit,

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
                },
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
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0B1E3A),
                                Color(0xFF051728),
                                Color(0xFF020A14)
                            )
                        )
                    )
            )
        }
        if (state.showAddMarkerDialog) {
            AddMarkerDialog(
                onDismiss = onAddMarkerDismiss,
                onRecordClick = onRecordClick
            )
        }
        if (state.showMicPermissionDialog) {
            PermissionSettingsDialog(
                onConfirm = onConfirmPermissionSettingsDialog,
                onDismiss = onDismissPermissionSettingsDialog
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMarkerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onRecordClick: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                IconButton(
                    onClick = onRecordClick
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardVoice,
                        contentDescription = "Record"
                    )
                }

            }
        }
    }
}


@Composable
fun PermissionSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Couldn't record audio")
        },
        text = {
            Text(
                "Please allow microphone access in App Settings."            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("To settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}