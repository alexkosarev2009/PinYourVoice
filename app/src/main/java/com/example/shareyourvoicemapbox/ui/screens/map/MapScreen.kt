@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shareyourvoicemapbox.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute
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
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.StandardStyleConfigurationState
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.time.LocalTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: MapViewModel = hiltViewModel<MapViewModel>(),
) {
    val state by viewModel.uiState.collectAsState()
    val systemState by viewModel.systemState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    val minDuration = viewModel.minDuration
    val maxDuration = viewModel.maxDuration
    val progress by remember {
        derivedStateOf {
            (systemState.recordTimeMs / maxDuration.toFloat()).coerceIn(0f, 1f)
        }
    }
    var overlayVisible by remember { mutableStateOf(true) }

    val animatedScale by animateFloatAsState(
        targetValue = if (systemState.isRecording) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "record_scale",
    )
    val view = LocalView.current

    DisposableEffect(Unit) {
        val window = (view.context as Activity).window


        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        onDispose {
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            viewModel.closeAddMarkerDialog()
            viewModel.onDeleteRecordingClick()
        }
    }
    val offset by animateDpAsState(
        targetValue = if (systemState.isRecording) 40.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "buttons_offset",
    )

    val markerIcon = rememberIconImage(
        key = "red-marker",
        painter =
            painterResource(id = R.drawable.red_marker),
    )

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted: Boolean ->
        viewModel.setLocationPermission(granted)
    }
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val fineLocationPermissionState =
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getData()
    }

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when (action) {
                is MapAction.OpenScreen -> {
                    val encodedPath = URLEncoder.encode(systemState.currentAudioPath, "UTF-8")

                    navHostController.navigate("${SecondaryRoute.EDIT.route}/${encodedPath}?lat=${systemState.userLocation?.latitude()}&lng=${systemState.userLocation?.longitude()}") {
                        launchSingleTop = true
                        restoreState = true
                    }

                }
            }
        }
    }
    LaunchedEffect(Unit) {
        val point = systemState.userLocation ?: return@LaunchedEffect

        if (systemState.hasCenteredUser) {
            systemState.mapViewportState.flyTo(
                cameraOptions {
                    center(point)
                    zoom(14.0)
                },
            )
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
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
        is MapState.Content -> {
            if (!isConnected) {
                Column(
                    modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,

                    ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        Icon(
                            modifier = Modifier,
                            imageVector = ImageVector.vectorResource(R.drawable.no_connection_icon),
                            contentDescription = "Connection status",
                            tint = Color.Gray,
                        )
                        Icon(
                            modifier = Modifier
                                .padding(8.dp, 8.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background),
                            tint = MaterialTheme.colorScheme.error,
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "No connection",
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "No internet connection",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.width(350.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(text = "Offline maps not supported")
                }

            } else {
                Scaffold(
                    modifier = modifier,
                    snackbarHost = {
                    },
                    floatingActionButton = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            FloatingActionButton(
                                modifier = Modifier.size(48.dp),
                                onClick = {
                                    when {
                                        fineLocationPermissionState.status.isGranted -> {
                                            systemState.mapViewportState.transitionToFollowPuckState(
                                                followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                                                    .pitch(0.0).build(),
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
                                shape = CircleShape,
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
                                    val locationGranted =
                                        fineLocationPermissionState.status.isGranted

                                    when {
                                        audioGranted && locationGranted -> {
                                            viewModel.openAddMarkerDialog()
                                            systemState.mapViewportState.transitionToFollowPuckState(
                                                followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                                                    .pitch(0.0).build(),
                                            )
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
                        LaunchedEffect(currentState.error) {
                            if (currentState.error != "") {
                                snackbarHostState.showSnackbar(
                                    message = "Couldn't load markers",
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
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
                                viewModel.onDeleteRecordingClick()
                                viewModel.closeAddMarkerDialog()
                            } else {
                                viewModel.onRecordRelease()
                                viewModel.onDeleteRecordingClick()
                                viewModel.closeAddMarkerDialog()
                            }
                        },
                        onConfirmPermissionSettingsDialog = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
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
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
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
                        minDuration = minDuration,
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize().statusBarsPadding().padding(0.dp, 8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    SnackbarHost(snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            shape = RoundedCornerShape(32.dp),
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.widthIn(max = 280.dp).statusBarsPadding()
                        )
                    }
                }
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
        contentAlignment = Alignment.TopCenter,
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(1000)),
        ) {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = systemState.mapViewportState,
                scaleBar = {},
                compass = {
                    Compass(modifier = Modifier.padding(0.dp, 120.dp))
                },
                logo = {
                    Logo()
                },
                style = {
                    val hour = rememberHour()
                    MapboxStandardStyle(
                        standardStyleConfigurationState = remember {
                            StandardStyleConfigurationState().apply {
                                lightPreset = when (hour) {
                                    in 6..11 -> LightPresetValue.DAWN
                                    in 12..17 -> LightPresetValue.DAY
                                    in 18..20 -> LightPresetValue.DUSK
                                    else -> LightPresetValue.NIGHT
                                }
                            }
                        }
                    )
                }
            ) {
                MapEffect(Unit) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                    }
                    if (!systemState.hasCenteredUser) {
                        systemState.mapViewportState.transitionToFollowPuckState(
                            followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                                .pitch(0.0).build(),
                        )
                    }

                    mapView.location.addOnIndicatorPositionChangedListener { point ->
                        onLocationChange(point)
                    }
                }
                state.markers.forEach { marker ->
                    val point = Point.fromLngLat(marker.lng, marker.lat)

                    PointAnnotation(
                        point = point,
                    ) {
                        iconImage = markerIcon
                        interactionsState.onClicked {
                            systemState.mapViewportState.flyTo(
                                cameraOptions {
                                    center(point)

                                },
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
            exit = fadeOut(animationSpec = tween(1000)),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0B1E3A),
                                Color(0xFF051728),
                                Color(0xFF020A14),
                            ),
                        ),
                    ),
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
                minDuration = minDuration,
            )
        }
        if (state.showMicPermissionDialog) {
            PermissionSettingsDialog(
                onConfirm = onConfirmPermissionSettingsDialog,
                onDismiss = onDismissPermissionSettingsDialog,
                title = "Couldn't record audio",
                text = "Please allow microphone access in App Settings.",
            )
        }
        if (state.showFineLocationPermissionDialog) {
            PermissionSettingsDialog(
                onConfirm = onConfirmFineLoactionDialog,
                onDismiss = onDismissFineLocationDialog,
                title = "Couldn't fetch your current location",
                text = "Please allow location access in App Settings.",
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
    minDuration: Long,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    onClick = onDeleteRecordingClick,
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = -(offset.plus(4.dp)).toPx()
                        },
                    enabled = systemState.recordTimeMs > 0,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete recording",
                        modifier = Modifier.size(32.dp),
                    )
                }
                Column(
                    modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
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
                                    },
                                )
                            }
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = Icons.Default.KeyboardVoice,
                            contentDescription = "Record",
                            tint = MaterialTheme.colorScheme.onPrimary,
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
                }

                IconButton(
                    onClick = onSaveRecordingClick,
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = (offset.plus(4.dp)).toPx()
                        },
                    enabled = systemState.recordTimeMs > minDuration,

                    ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save recording",
                        modifier = Modifier.size(32.dp),
                    )
                }

            }
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier.offset(y = offset / 2),
                text = formatTime(systemState.recordTimeMs),
                fontSize = 20.sp,
                color = if (systemState.recordTimeMs != 0L && systemState.recordTimeMs < minDuration) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            Spacer(Modifier.height(42.dp))
        }
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
        },
    )
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(minutes, seconds)
}

@Composable
fun rememberHour(): Int {
    var hour by remember { mutableStateOf(LocalTime.now().hour) }

    LaunchedEffect(Unit) {
        while (true) {
            hour = LocalTime.now().hour
            Log.d("HOUR", hour.toString())
            delay(60_000)
        }
    }

    return hour
}