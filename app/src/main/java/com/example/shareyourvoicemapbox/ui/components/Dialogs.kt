@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shareyourvoicemapbox.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.example.shareyourvoicemapbox.ui.screens.map.MapSystemState
import com.example.shareyourvoicemapbox.ui.screens.map.formatTime
import java.time.Instant

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
                Modifier.fillMaxWidth(),
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
                        contentDescription = stringResource(R.string.delete_recording),
                        modifier = Modifier.size(32.dp),
                    )
                }
                Column(
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
                            contentDescription = stringResource(R.string.record),
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
                        contentDescription = stringResource(R.string.save_recording),
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
fun ViewMarkerDialog(
    marker: MarkerEntity,
    onDismiss: () -> Unit,
    onMenuClick: () -> Unit,
    onPlayClick: (String) -> Unit,
    isPLaying: Boolean,
    onNameClick: (String) -> Unit = {},
    onReportClick: (Long) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = marker.authorAvatarUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                )

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = marker.authorName,
                        modifier = Modifier.clickable(
                            onClick = { onNameClick(marker.authorUsername) },
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "@${marker.authorUsername}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                }

                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier,
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.report)) },
                            leadingIcon = { Icon(Icons.Default.Flag, null) },
                            onClick = {
                                onReportClick(marker.id)
                                expanded = false
                            },
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(16.dp, 0.dp))
            Row(
                verticalAlignment = Alignment.Top,
            ) {
                AsyncImage(
                    model = marker.imageUrl,
                    contentDescription = stringResource(R.string.image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(16.dp, 16.dp, 8.dp, 16.dp)
                        .heightIn(max = 140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 16.dp, 8.dp, 16.dp)
                        .height(140.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Column() {
                        Text(
                            text = marker.title,
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Text(
                            text = marker.location,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        val instant = Instant.parse(marker.createdAt).toEpochMilli()
                        val now = System.currentTimeMillis()
                        val relativeText = DateUtils.getRelativeTimeSpanString(
                            instant,
                            now,
                            DateUtils.MINUTE_IN_MILLIS,
                        ).toString()
                        Spacer(Modifier.height(4.dp))
                        Text(
                            relativeText,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(
                                onClick = { onPlayClick(marker.audioUrl) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector =
                                if (!isPLaying) Icons.Default.PlayArrow
                                else Icons.Default.Pause,
                            contentDescription = stringResource(R.string.play_audio),
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(36.dp),
                        )
                    }


                }
            }
            Spacer(Modifier.height(8.dp))
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
                Text(stringResource(R.string.to_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
