@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shareyourvoicemapbox.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute
import com.example.shareyourvoicemapbox.ui.theme.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import kotlinx.coroutines.delay
import kotlin.math.min

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: MapViewModel = hiltViewModel<MapViewModel>()
) {
    val state by viewModel.uiState.collectAsState()
    val systemState by viewModel.systemState.collectAsState()
    val minDuration = viewModel.minDuration
    val maxDuration = viewModel.maxDuration
    val progress = (systemState.recordTimeMs / maxDuration.toFloat())
        .coerceIn(0f, 1f)


    var overlayVisible by remember { mutableStateOf(true) }

    val animatedScale by animateFloatAsState(
        targetValue = if (systemState.isRecording) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "record_scale"
    )
    val offset by animateDpAsState(
        targetValue = if (systemState.isRecording) 40.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttons_offset"
    )

    val markerIcon = rememberIconImage(key = "red-marker", painter =
        painterResource(id = R.drawable.red_marker))

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted: Boolean ->
        viewModel.setLocationPermission(granted)
    }
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val fineLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when (action) {
                is MapAction.OpenScreen -> {
                    navHostController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("audioPath", systemState.currentAudioPath)
                    navHostController.navigate(SecondaryRoute.EDIT.route)

                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            viewModel.setLocationPermission(true)
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

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
                modifier = modifier,
                floatingActionButton = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FloatingActionButton(
                            modifier = Modifier.size(48.dp),
                            onClick = {
                                when {
                                    fineLocationPermissionState.status.isGranted -> {
                                        currentState.mapViewportState.transitionToFollowPuckState(
                                            followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder().pitch(0.0).build()
                                        )
                                    }
                                    fineLocationPermissionState.status.shouldShowRationale -> {
                                        viewModel.openFineLocationPermissionDialog()
                                    }
                                    else -> {
                                        val isPermanentlyDenied =
                                            !fineLocationPermissionState.status.shouldShowRationale

                                        if (isPermanentlyDenied) {
                                            viewModel.openFineLocationPermissionDialog()
                                        } else {
                                            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                        }
                                    }
                                }
                            },
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = "My location",
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        FloatingActionButton(
                            onClick = {
                                val audioGranted = audioPermissionState.status.isGranted
                                val locationGranted = fineLocationPermissionState.status.isGranted

                                when {
                                    audioGranted && locationGranted -> {
                                        viewModel.openAddMarkerDialog()
                                    }

                                    !audioGranted -> {
                                        when {
                                            audioPermissionState.status.shouldShowRationale -> {
                                                viewModel.openMicPermissionDialog()
                                            }
                                            else -> {
                                                launcher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        }
                                    }
                                    !locationGranted -> {
                                        when {
                                            fineLocationPermissionState.status.shouldShowRationale -> {
                                                viewModel.openFineLocationPermissionDialog()
                                            }

                                            else -> {
                                                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                            }
                                        }
                                    }
                                }
                            },
                            shape = CircleShape,
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddLocationAlt,
                                contentDescription = "Add marker",
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }

                },
            ) {
                MapContent(
                    state = currentState,
                    overlayVisible = overlayVisible,
                    onMarkerClick = {

                    },
                    onAddMarkerDismiss = {
                        if (!systemState.isRecording) {
                            viewModel.closeAddMarkerDialog()
                        }
                        else {
                            viewModel.onRecordRelease()
                            viewModel.closeAddMarkerDialog()
                        }
                    },
                    onConfirmPermissionSettingsDialog = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        viewModel.closeMicPermissionDialog()
                    },
                    onDismissPermissionSettingsDialog = {
                        viewModel.closeMicPermissionDialog()
                    },
                    onRecordClick = {
                        viewModel.onRecordClick()
                    },
                    onRecordRelease = {
                        viewModel.onRecordRelease()
                    },
                    animatedScale = animatedScale,
                    markerIcon = markerIcon,
                    onLocationChange = { point ->
                        viewModel.updateUserLocation(point)
                    },
                    onConfirmFineLoactionDialog = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        viewModel.closeFineLocationPermissionDialog()
                    },
                    onDismissFineLocationDialog = {
                        viewModel.closeFineLocationPermissionDialog()
                    },
                    offset = offset,
                    systemState = systemState,
                    onSaveRecordingClick = {
                        viewModel.onRecordRelease()
                        viewModel.onSaveRecordingClick()
                    },
                    onDeleteRecordingClick = {
                        viewModel.onRecordRelease()
                        viewModel.onDeleteRecordingClick()
                    },
                    progress = progress,
                    minDuration = minDuration
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
    onRecordRelease: () -> Unit,
    animatedScale: Float,
    markerIcon: IconImage,
    onLocationChange: (Point) -> Unit,
    onConfirmFineLoactionDialog: () -> Unit,
    onDismissFineLocationDialog: () -> Unit,
    offset: Dp,
    systemState: MapSystemState,
    onSaveRecordingClick: () -> Unit,
    onDeleteRecordingClick: () -> Unit,
    progress: Float,
    minDuration: Long,
) {
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
                MapEffect(Unit) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                    }
                    state.mapViewportState.transitionToFollowPuckState(
                        followPuckViewportStateOptions = FollowPuckViewportStateOptions
                            .Builder().pitch(0.0).build()
                    )
                    mapView.location.addOnIndicatorPositionChangedListener { point ->
                        onLocationChange(point)
                    }
                }
                state.markers.forEach { marker ->
                    val point = Point.fromLngLat(marker.lng, marker.lat)
                    PointAnnotation(
                        point = point
                    ) {
                        iconImage = markerIcon
                        interactionsState.onClicked {
                            state.mapViewportState.flyTo(
                                cameraOptions {
                                    center(point)

                                }
                            )
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
                onRecordClick = onRecordClick,
                animatedScale = animatedScale,
                onRecordRelease = onRecordRelease,
                offset = offset,
                systemState = systemState,
                onSaveRecordingClick = onSaveRecordingClick,
                onDeleteRecordingClick = onDeleteRecordingClick,
                progress = progress,
                minDuration = minDuration
            )
        }
        if (state.showMicPermissionDialog) {
            PermissionSettingsDialog(
                onConfirm = onConfirmPermissionSettingsDialog,
                onDismiss = onDismissPermissionSettingsDialog,
                title = "Couldn't record audio",
                text = "Please allow microphone access in App Settings."
            )
        }
        if (state.showFineLocationPermissionDialog) {
            PermissionSettingsDialog(
                onConfirm = onConfirmFineLoactionDialog,
                onDismiss = onDismissFineLocationDialog,
                title = "Couldn't fetch your current location",
                text = "Please allow location access in App Settings."
            )
        }

    }
}

@Composable
fun AddMarkerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onRecordClick: () -> Unit,
    animatedScale: Float,
    onRecordRelease: () -> Unit,
    offset: Dp,
    systemState: MapSystemState,
    onSaveRecordingClick: () -> Unit,
    onDeleteRecordingClick: () -> Unit,
    progress: Float,
    minDuration: Long
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onDeleteRecordingClick,
                    modifier.offset(x = -(offset.plus(4.dp))),
                    enabled = systemState.isRecording || systemState.currentAudioPath != null
                ) {
                    Icon(imageVector = Icons.Default.Delete,
                        contentDescription = "Delete recording",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Column(
                    modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(animatedScale)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        onRecordClick()
                                        val pressTime = System.currentTimeMillis()
                                        val released = tryAwaitRelease()
                                        val duration = System.currentTimeMillis() - pressTime

                                        if (released) {
                                            if (duration > 500) {
                                                onRecordRelease()
                                            }
                                        }
                                    }
                                )
                            }
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = Icons.Default.KeyboardVoice,
                            contentDescription = "Record",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        CircularProgressIndicator(
                            progress = {
                                progress
                            },
                            modifier = Modifier.size(70.dp),
                            trackColor = MaterialTheme.colorScheme.primaryContainer,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,

                            )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(modifier = Modifier.offset(y = offset / 2),
                        text = formatTime(systemState.recordTimeMs),
                        fontSize = 20.sp,
                        color = if (systemState.recordTimeMs < minDuration && systemState.isRecording) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                IconButton(
                    onClick = onSaveRecordingClick,
                    modifier = Modifier.offset(x = offset.plus(4.dp)),
                    enabled = systemState.isRecording || systemState.currentAudioPath != null

                ) {
                    Icon(imageVector = Icons.Default.Check,
                        contentDescription = "Save recording",
                        modifier = Modifier.size(32.dp)
                    )
                }

            }
            Spacer(Modifier.height(42.dp))
        }
    }
}

@Composable
@Preview
fun AddMarkerDialogPreview(modifier: Modifier = Modifier) {
    AppTheme {
        AddMarkerDialog(
            onDismiss = {},
            onRecordClick = {},
            animatedScale = 1f,
            onRecordRelease = {},
            offset = 0.dp,
            systemState = MapSystemState(),
            onSaveRecordingClick = {},
            onDeleteRecordingClick = {},
            progress = 0f,
            minDuration = 1
        )
    }
}


@Composable
fun PermissionSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String,

) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
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

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(minutes, seconds)
}