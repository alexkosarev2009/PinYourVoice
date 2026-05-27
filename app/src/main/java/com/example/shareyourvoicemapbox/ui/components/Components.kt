package com.example.shareyourvoicemapbox.ui.components

import android.text.format.DateUtils
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.shareyourvoicemapbox.domain.amplituda.ParseAmplitudesUseCase
import com.example.shareyourvoicemapbox.domain.entities.InvitationEntity
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.example.shareyourvoicemapbox.domain.entities.UserEntity
import com.example.shareyourvoicemapbox.ui.theme.AppTheme
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.WaveformAlignment
import java.time.Instant

@Composable
fun MarkerCard(
    modifier: Modifier = Modifier,
    title: String,
    id: Long,
    location: String,
    username: String,
    avatarUrl: String,
    imageUrl: String,
    onPlayClick: () -> Unit,
    onOpenMap: () -> Unit,
    amplitudes: List<Int>,
    onMenuClick: () -> Unit,
    waveformProgress: Float,
    onWaveformProgressChange: (Float) -> Unit,
    name: String,
    createdAt: String,
    audioUrl: String,
    isPLaying: Boolean = false,
    onReportClick: (Long) -> Unit,
    onNameClick: () -> Unit = {}
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
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable(
                            onClick = onNameClick
                        )
                    )
                    Text(
                        text = "@$username",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Report") },
                            leadingIcon = { Icon(Icons.Default.Flag, null) },
                            onClick = {
                                onReportClick(id)
                                expanded = false
                            },
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(0.dp, 0.dp))
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentScale = ContentScale.FillWidth
            )
            HorizontalDivider(modifier = Modifier.padding(0.dp, 0.dp))
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
                    val imageVector = if (isPLaying)
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
                    spikeAnimationSpec = tween(500)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val instant = Instant.parse(createdAt).toEpochMilli()
                val now = System.currentTimeMillis()
                val relativeText = DateUtils.getRelativeTimeSpanString(
                    instant,
                    now,
                    DateUtils.MINUTE_IN_MILLIS
                ).toString()


                Text(relativeText, color = Color.Gray)

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
    AppTheme(
        darkTheme = false
    ) {
        MarkerCard(
            title = "Moscow City",
            location = "Moscow, Russia",
            username = "k1riesshka",
            avatarUrl = "https://i.pinimg.com/236x/68/31/12/68311248ba2f6e0ba94ff6da62eac9f6.jpg",
            imageUrl = "",
            onPlayClick = {},
            onOpenMap = {},
            amplitudes = listOf(1, 2, 6, 15, 4, 7, 12, 24),
            waveformProgress = 0.7f,
            onWaveformProgressChange = {},
            name = "Саша Косарев",
            createdAt = "2026-04-25T19:48:26.812081Z",
            audioUrl = "",
            onMenuClick = {},
            id = 1L,
            onNameClick = {},
            onReportClick = {}
        )
    }
}

@Composable
fun ShortMarkerCard(
    modifier: Modifier = Modifier,
    marker: MarkerEntity,
    onMenuClick: () -> Unit,
    waveformProgress: Float,
    onWaveformProgressChange: (Float) -> Unit,
    onPlayClick: () -> Unit,
    isPLaying: Boolean,
    onOpenMap: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    onReportClick: (Long) -> Unit,
    isDeleteVisible: Boolean = false
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = marker.authorAvatarUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = marker.authorName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "@${marker.authorUsername}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (isDeleteVisible) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Delete Marker",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete, null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },

                                onClick = {
                                    onDeleteClick(marker.id)
                                    expanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Report") },
                            leadingIcon = { Icon(Icons.Default.Flag, null) },
                            onClick = {
                                onReportClick(marker.id)
                                expanded = false
                            },
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(0.dp, 0.dp))
            Row(
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = marker.imageUrl,
                    contentDescription = "image",
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
                    horizontalAlignment = Alignment.Start
                ) {
                    Column() {
                        Text(
                            text = marker.title,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = marker.location,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
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
                    val instant = Instant.parse(marker.createdAt).toEpochMilli()
                    val now = System.currentTimeMillis()
                    val relativeText = DateUtils.getRelativeTimeSpanString(
                        instant,
                        now,
                        DateUtils.MINUTE_IN_MILLIS
                    ).toString()


                    Text(relativeText, color = Color.Gray)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(0.dp, 0.dp))
            Spacer(Modifier.height(8.dp ))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                    val imageVector = if (isPLaying)
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
                    amplitudes = ParseAmplitudesUseCase().invoke(marker.amplitudes).map {
                        it + 3
                    },
                    progress = waveformProgress,
                    onProgressChange = onWaveformProgressChange,
                    spikeWidth = 4.dp,
                    spikeRadius = 32.dp,
                    waveformAlignment = WaveformAlignment.Center,
                    spikeAnimationSpec = tween(500)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
@Preview
fun ShortMarkerCardPreview() {
    AppTheme {
        ShortMarkerCard(
            marker = MarkerEntity(
                lat = 1.0,
                lng = 1.0,
                title = "Moscow City",
                location = "Moscow, Russia",
                authorUsername = "k1riesshka",
                authorAvatarUrl = "https://i.pinimg.com/236x/68/31/12/68311248ba2f6e0ba94ff6da62eac9f6.jpg",
                imageUrl = "",
                amplitudes = listOf(1, 2, 6, 15, 4, 7, 12, 24).toString(),
                authorName = "Саша Косарев",
                createdAt = "2026-04-25T19:48:26.812081Z",
                audioUrl = "",
                icon = 1
            ),
            onMenuClick = {},
            waveformProgress = 0.7f,
            onWaveformProgressChange = {},
            onPlayClick = {},
            isPLaying = false,
            onOpenMap = {},
            onDeleteClick = {},
            onReportClick = {}
        )
    }
}

@Composable
fun InvitationCard(
    invitation: InvitationEntity,
    modifier: Modifier = Modifier,
    onNameClick: () -> Unit = {},
    onAcceptClick: (Long) -> Unit,
    onDeclineClick: (Long) -> Unit
) {
    ElevatedCard() {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = invitation.senderAvatarUrl,
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
                        text = invitation.senderName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable(
                            onClick = onNameClick,
                        ),
                    )
                    Text(
                        text = "@${invitation.senderUsername}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                }
                IconButton(
                    onClick = {
                        onAcceptClick(invitation.id)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)

                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept invitation"
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(
                    onClick = {
                        onDeclineClick(invitation.id)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = "Accept invitation"
                    )
                }

            }
            val instant = Instant.parse(invitation.createdAt).toEpochMilli()
            val now = System.currentTimeMillis()
            val relativeText = DateUtils.getRelativeTimeSpanString(
                instant,
                now,
                DateUtils.MINUTE_IN_MILLIS
            ).toString()

            Spacer(Modifier.height(8.dp))

            Text(relativeText, color = Color.Gray)
        }
    }
}

@Composable
fun FriendCard(
    friend: UserEntity,
    modifier: Modifier = Modifier,
    onNameClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    ElevatedCard() {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = friend.avatarUrl,
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
                        text = friend.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable(
                            onClick = onNameClick,
                        ),
                    )
                    Text(
                        text = "@${friend.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp),
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Accept invitation"
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun FriendCardPreview(modifier: Modifier = Modifier) {
    AppTheme() {
        FriendCard(
            friend = UserEntity(
                id = 0,
                name = "Саша Косарев",
                username = "k1riesshka",
                bio = "Empty bio",
                avatarUrl = "",
            ),
            onNameClick = {},
            onAddClick = {}
        )
    }
}

@Composable
@Preview
fun InvitationCardPreview(modifier: Modifier = Modifier) {
    AppTheme {
        InvitationCard(
            invitation = InvitationEntity(
                senderId = 1,
                senderName = "Cаша Косарев",
                senderUsername = "k1riesshka",
                senderAvatarUrl = "TODO()",
                createdAt = "2026-04-25T19:48:26.812081Z"
            ),
            onAcceptClick = {},
            onNameClick = {},
            onDeclineClick = {}
        )
    }
}

@Composable
fun MinimalDropdownMenu(

) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Delete Marker", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.Delete, null,
                    tint = MaterialTheme.colorScheme.error) },

                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text("Report") },
                leadingIcon = { Icon(Icons.Default.Flag, null) },
                onClick = { /* Do something... */ },
            )
        }
    }
}