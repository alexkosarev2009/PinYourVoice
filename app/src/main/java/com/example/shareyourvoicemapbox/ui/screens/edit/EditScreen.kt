package com.example.shareyourvoicemapbox.ui.screens.edit

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.linc.audiowaveform.AudioWaveform

@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: EditViewModel = hiltViewModel<EditViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    var waveformProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        if (state.audioPath == "") return@LaunchedEffect
        viewModel.processAudio()
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
            waveformProgress = waveformProgress,
            amplitudes = state.amplitudes,
            onWaveformProgressChange = {

            },
        )
    }

}

@Composable
fun EditContent(
    modifier: Modifier = Modifier,
    state: EditState,
    waveformProgress: Float,
    amplitudes: List<Int>,
    onWaveformProgressChange: (Float) -> Unit
) {
    Column(
        modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = {
                val mediaPlayer = MediaPlayer().apply {
                    setDataSource(state.audioPath)
                    prepare()
                    start()
                }
            },
        ) {
            Text("PLAY")
        }

        AudioWaveform(
            modifier = Modifier
                .height(100.dp),
            progressBrush = Brush.horizontalGradient(listOf(Color.Cyan, Color.Blue)),
            waveformBrush = SolidColor(MaterialTheme.colorScheme.primaryContainer),
            amplitudes = amplitudes,
            progress = waveformProgress,
            onProgressChange = onWaveformProgressChange,

            )
        Text(if (state.audioPath != "") state.audioPath else "No audio")
    }
}