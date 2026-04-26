package com.example.shareyourvoicemapbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.shareyourvoicemapbox.ui.screens.edit.PlayerState
import com.example.shareyourvoicemapbox.ui.theme.AppTheme
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.WaveformAlignment

@Composable
fun MarkerCard(
    modifier: Modifier = Modifier,
    title: String,
    location: String,
    username: String,
    avatarUrl: String,
    imageUrl: String,
    onPlayClick: () -> Unit,
    onOpenMap: () -> Unit,
    amplitudes: List<Int>,
    waveformProgress: Float,
    onWaveformProgressChange: (Float) -> Unit,
    playerState: PlayerState,
    name: String,
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = avatarUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "@$username",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = { /* menu */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }
            }
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = location,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    val imageVector = if (playerState.isPlaying)
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
                    waveformBrush = SolidColor(MaterialTheme.colorScheme.surfaceContainerHighest),
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
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(0.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(
                        onClick = onOpenMap
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Map,
                        contentDescription = "Go to map",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = false)
fun MarkerCardPreview(modifier: Modifier = Modifier) {
    AppTheme() {
        MarkerCard(
            title = "Moscow City",
            location = "Moscow, Russia",
            username = "k1riesshka",
            avatarUrl = "https://i.pinimg.com/236x/68/31/12/68311248ba2f6e0ba94ff6da62eac9f6.jpg",
            imageUrl = "",
            onPlayClick = {},
            onOpenMap = {},
            amplitudes = listOf(1, 2, 6, 15, 4, 7, 12, 24),
            waveformProgress = 0f,
            onWaveformProgressChange = {},
            playerState = PlayerState(),
            name = "Саша Косарев"
        )
    }
}