package com.example.shareyourvoicemapbox.ui.screens.edit

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.shareyourvoicemapbox.ui.navigation.Route
import com.example.shareyourvoicemapbox.ui.theme.AppTheme
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.WaveformAlignment
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: EditViewModel = hiltViewModel<EditViewModel>(),
) {
    val state by viewModel.state.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val context = LocalContext.current

    val waveformProgress by remember {
        derivedStateOf {
            (playerState.currentPosition / playerState.maxDuration.toFloat())
                .coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(state.isDone) {
        if (state.isDone) {
            navHostController.popBackStack()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            viewModel.onImagePicked(uri)
            viewModel.getFileFromUri(context, uri)
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val density = LocalDensity.current
    val maxDragPx = with(density) { 250.dp.toPx() }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val imageScale = remember { Animatable(1f) }
    val shouldEnlarge = offsetX.value >= maxDragPx * 0.9f

    LaunchedEffect(shouldEnlarge) {
        imageScale.animateTo(
            targetValue = if (shouldEnlarge) 1.2f else 1f,
            animationSpec = tween(300),
        )
    }

    LaunchedEffect(Unit) {
        if (state.audioPath == "") return@LaunchedEffect
        viewModel.processAudio()
        viewModel.getAudioDurationMs(state.audioPath)
    }
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            Row(
                Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = {
                        navHostController.popBackStack()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Go back",
                    )
                }
            }
        },
    ) { innerPadding ->
        EditContent(
            modifier = modifier.padding(innerPadding),
            state = state,
            platerState = playerState,
            waveformProgress = waveformProgress,
            amplitudes = state.amplitudes,
            onWaveformProgressChange = { progress ->
                viewModel.onWaveformProgressChange(progress)
            },
            onImagePickClick = {
                if (state.imageUri == null) {
                    imagePicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )
                }
            },
            onTitleChange = { title ->
                viewModel.onTitleChange(title)
            },
            onPlayClick = {
                if (!playerState.isPlaying) {
                    viewModel.playAudio()
                } else {
                    viewModel.pauseAudio()
                }
            },
            titleState = state.title,
            onPostClick = {
                viewModel.onPostClick()
            },
            isPinYourVoiceEnabled = viewModel.isPinYourVoiceEnabled,
            offsetX = offsetX,
            maxDragPx = maxDragPx,
            onDrag = { change, dragAmount ->
                if (maxDragPx >= offsetX.value + dragAmount.x && dragAmount.x + offsetX.value >= 0f) {
                    change.consume()
                    scope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount.x)
                    }
                }
            },
            onDragEnd = {
                scope.launch {
                    if (offsetX.value > maxDragPx - 80) {
                        viewModel.onDeleteImage()
                    }
                    offsetX.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(800),
                    )
                }
            },
            imageScale = imageScale,
        )
    }
    LaunchedEffect(state.error) {
        if (state.error != "") {
            snackbarHostState.showSnackbar(
                message = state.error,
                duration = SnackbarDuration.Short,
                withDismissAction = true,
            )
        }
    }
    SnackbarHost(
        hostState = snackbarHostState,
    ) { data ->
        Snackbar(data)
    }

}

@Composable
fun EditContent(
    modifier: Modifier = Modifier,
    state: EditState,
    platerState: EditPlayerState,
    waveformProgress: Float,
    amplitudes: List<Int>,
    onWaveformProgressChange: (Float) -> Unit,
    onImagePickClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onPlayClick: () -> Unit,
    titleState: String,
    onPostClick: () -> Unit,
    isPinYourVoiceEnabled: Boolean,
    offsetX: Animatable<Float, AnimationVector1D>,
    maxDragPx: Float,
    onDrag: (PointerInputChange, Offset) -> Unit,
    onDragEnd: () -> Unit,
    imageScale: Animatable<Float, AnimationVector1D>,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {

            HorizontalDivider(
                modifier = Modifier
                    .padding(16.dp, 0.dp)
                    .zIndex(-1f),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val mod = Modifier
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .graphicsLayer {
                        scaleY = imageScale.value
                        scaleX = imageScale.value
                    }
                    .zIndex(10f)
                Box(
                    modifier = if (state.imageUri != null) mod
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = onDrag,
                                onDragEnd = onDragEnd,
                            )
                        }
                    else mod,

                    ) {
                    Box(
                        Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .clickable(
                                onClick = onImagePickClick,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state.imageUri == null) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add picture",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(32.dp),
                            )
                        } else {
                            AsyncImage(
                                model = state.imageUri,
                                contentDescription = "Preview photo",
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(40.dp, 0.dp)
                        .graphicsLayer {
                            alpha = (offsetX.value / maxDragPx).coerceIn(0.25f, 1f)
                            scaleX = (1 / (offsetX.value / maxDragPx)).coerceIn(1f, 1.3f)
                            scaleY = (1 / (offsetX.value / maxDragPx)).coerceIn(1f, 1.3f)
                        }
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),

                    ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onError,
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete image",
                    )
                }

            }
            HorizontalDivider(
                modifier = Modifier
                    .padding(16.dp, 0.dp)
                    .zIndex(-1f),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
            )

        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .widthIn(max = 320.dp),
                singleLine = false,
                value = titleState,
                onValueChange = onTitleChange,
                placeholder = {
                    Text(
                        "Add a title",
                        fontSize = 16.sp,
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = MaterialTheme.colorScheme.background,

                    ),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),

                )
            Text(
                text = "${titleState.length}/${MAX_TITLE_LEN}",
                fontWeight = FontWeight.Light,
            )
        }
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        ),
                    )
                    .padding(8.dp, 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer)
                        .clickable(
                            onClick = onPlayClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    val imageVector = if (platerState.isPlaying)
                        Icons.Default.Pause else Icons.Default.PlayArrow
                    Icon(
                        imageVector = imageVector,
                        contentDescription = "Play audio",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(32.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
                AudioWaveform(
                    modifier = Modifier,
                    progressBrush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            MaterialTheme.colorScheme.secondary,
                        ),
                    ),
                    waveformBrush = SolidColor(MaterialTheme.colorScheme.surfaceContainerLowest),
                    amplitudes = amplitudes.map {
                        it + 3
                    },
                    progress = waveformProgress,
                    onProgressChange = onWaveformProgressChange,
                    spikeWidth = 4.dp,
                    spikeRadius = 32.dp,
                    waveformAlignment = WaveformAlignment.Center,
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Button(
                onClick = onPostClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = isPinYourVoiceEnabled,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!state.isLoading) {
                        Icon(
                            imageVector = Icons.Default.PinDrop,
                            contentDescription = "Pin drop",
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Pin your voice", fontSize = 20.sp)
                    } else {
                        CircularProgressIndicator(
                            trackColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun EditScreenPreview(modifier: Modifier = Modifier) {
    AppTheme(
        darkTheme = false,
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                Row(
                    Modifier.fillMaxWidth(),
                ) {
                    IconButton(
                        onClick = {
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Go back",
                        )
                    }
                }
            },
        ) { innerPadding ->
            EditContent(
                modifier = modifier.padding(innerPadding),
                state = EditState(isLoading = true),
                platerState = EditPlayerState(),
                waveformProgress = 1f,
                amplitudes = listOf(
                    2, 3, 2, 4, 3, 2, 3, 2,
                    20, 35, 50, 65, 80, 75, 70, 85, 90, 78, 60, 55, 40, 30,
                    5, 3, 2, 2, 3,
                    40, 90, 150, 210, 180, 120, 60,
                    4, 3, 2,
                    60, 80, 110, 140, 170, 200, 220, 210, 190, 175, 160, 140, 120, 100, 85, 70, 55,
                    30, 20, 10, 6, 4, 3, 2,
                    25, 45, 70, 95, 130, 160, 140, 155, 120, 100, 80, 60,
                    3, 2, 2, 3, 2, 2,
                    50, 100,
                ),
                onWaveformProgressChange = {},
                onImagePickClick = {},
                onTitleChange = {},
                onPlayClick = {},
                titleState = "",
                onPostClick = {},
                isPinYourVoiceEnabled = true,
                offsetX = remember { Animatable(0f) },
                onDrag = { change, dragAmount -> },
                onDragEnd = {},
                maxDragPx = 250f,
                imageScale = remember { Animatable(1f) },
            )
        }
    }
}