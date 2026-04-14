package com.example.shareyourvoicemapbox.ui.screens.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.ui.theme.AppTheme
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.WaveformAlignment


@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: EditViewModel = hiltViewModel<EditViewModel>(),
) {
    val state by viewModel.state.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    val waveformProgress by remember {
        derivedStateOf {
            (playerState.currentPosition / playerState.maxDuration.toFloat())
                .coerceIn(0f, 1f)
        }
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
                Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        navHostController.popBackStack()
                    }
                ) {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Go back")
                }
            }
        }
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

            },
            onTitleChange = { title ->
                viewModel.onTitleChange(title)
            },
            onPlayClick = {
                if (!playerState.isPlaying) {
                    viewModel.playAudio()
                }
                else {
                    viewModel.pauseAudio()
                }
            },
            titleState = state.title,
            onPostClick = {

            }
        )
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
    onPostClick: () -> Unit
) {
    Column(
        modifier
            .fillMaxSize().padding(16.dp, 0.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 4.dp),
        ) {
            Box(
                Modifier.size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .clickable(
                        onClick = onImagePickClick,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add picture",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp),
                    )
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.heightIn(min = 52.dp).widthIn(max = 320.dp),
                singleLine = false,
                value = titleState,
                onValueChange = onTitleChange,
                placeholder = {
                    Text("Add a title",
                        fontSize = 16.sp)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor =  MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = MaterialTheme.colorScheme.background,

                    ),
                textStyle = TextStyle(fontWeight = FontWeight.Bold,
                    fontSize = 16.sp),



            )
            Text(text = "${titleState.length}/${MAX_TITLE_LEN}",
                fontWeight = FontWeight.Light)
        }
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp, 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer)
                        .clickable(
                            onClick = onPlayClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val imageVector = if (platerState.isPlaying)
                        Icons.Default.Pause else Icons.Default.PlayArrow
                    Icon(imageVector = imageVector,
                        contentDescription = "Play audio",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.width(16.dp))
                AudioWaveform(
                    modifier = Modifier,
                    progressBrush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            MaterialTheme.colorScheme.secondary
                        )
                    ),
                    waveformBrush = SolidColor(MaterialTheme.colorScheme.surfaceContainerLowest),
                    amplitudes = amplitudes.map { it ->
                        if (it == 0) it + 1 else it
                    },
                    progress = waveformProgress,
                    onProgressChange = onWaveformProgressChange,
                    spikeWidth = 4.dp,
                    spikeRadius = 32.dp,
                    waveformAlignment = WaveformAlignment.Center
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = onPostClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                Text("Post marker", fontSize = 20.sp)
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun EditScreenPreview(modifier: Modifier = Modifier) {
    AppTheme(
        darkTheme = false
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Go back"
                        )
                    }
                }
            }
        ) { innerPadding ->
            EditContent(
                modifier = modifier.padding(innerPadding),
                state = EditState(),
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
                    50, 100),
                onWaveformProgressChange = {},
                onImagePickClick = {},
                onTitleChange = {},
                onPlayClick = {},
                onPostClick = {},
                titleState = "",
            )
        }
    }
}