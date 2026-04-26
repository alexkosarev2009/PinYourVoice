@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shareyourvoicemapbox.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SwipeUpAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.example.shareyourvoicemapbox.ui.components.MarkerCard
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute
import com.example.shareyourvoicemapbox.ui.screens.edit.PlayerState
import com.example.shareyourvoicemapbox.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>()
    ) {

    val state by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()

    val refreshState = rememberPullToRefreshState()

    val scope = rememberCoroutineScope()

    val showScrollTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex >= 2
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            viewModel.loadUserInfo()
        },
        state = refreshState,
        indicator = {
            Indicator(
                state = refreshState,
                isRefreshing = state.isRefreshing,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(24.dp, 8.dp, 24.dp, 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                    Row {
                        IconButton(
                            onClick = {

                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                            )
                        }

                        Spacer(Modifier.width(4.dp))

                        IconButton(
                            onClick = {
                                viewModel.onMenuClick()
                                navHostController.navigate(SecondaryRoute.AUTH.route) {
                                    popUpTo(0)
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                            )
                        }
                    }
                }
            }
            item {
                ProfileContent(
                    state = state,
                    onMarkersClick = {
                        scope.launch {
                            listState.animateScrollToItem(
                                3,
                                scrollOffset = -168,
                            )
                        }
                    },
                )
            }
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                        .padding(24.dp, 0.dp, 24.dp, 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Markers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                    Column() {
                        Spacer(Modifier.height(4.dp))
                        AnimatedVisibility(
                            visible = showScrollTop,
                            enter = fadeIn(tween(500)),
                            exit = fadeOut(tween(500)),
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Scroll up",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        onClick = {
                                            scope.launch {
                                                listState.animateScrollToItem(0)
                                            }
                                        },
                                    ),
                            )
                        }

                    }

                }
            }
            items(state.markers) { marker ->
                MarkerCard(
                    modifier = Modifier.padding(24.dp, 0.dp),
                    title = marker.title,
                    location = marker.location,
                    username = marker.authorUsername,
                    avatarUrl = marker.authorAvatarUrl,
                    imageUrl = marker.imageUrl ?: "",
                    onPlayClick = { },
                    onOpenMap = { },
                    amplitudes = emptyList(),
                    waveformProgress = 0f,
                    onWaveformProgressChange = {},
                    playerState = PlayerState(),
                    name = marker.authorName,
                    createdAt = marker.createdAt
                )
                Spacer(Modifier.height(20.dp))
            }
            item {
                if (state.markers == emptyList<MarkerEntity>()) {
                    Text(
                        "No markers yet",
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileState,
    onMarkersClick: () -> Unit,
    ) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {

                Icon(imageVector = Icons.Default.Person,
                    contentDescription = "default avatar",
                    modifier = Modifier.size(68.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                if (state.avatarUrl != "") {
                    AsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        model = state.avatarUrl,
                        contentDescription = "user avatar",
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(state.fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("@${state.userName}", fontSize = 16.sp)
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .padding(24.dp, 0.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                modifier = Modifier
                    .height(80.dp)
                    .weight(1f)
                    .clickable(
                        onClick = onMarkersClick
                    ),
                colors = CardDefaults.elevatedCardColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.TopEnd),
                        imageVector = Icons.Default.Place,
                        contentDescription = "marker",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(8.dp)
                    ) {
                        Text(state.markers.size.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Markers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
            Spacer(Modifier.width(24.dp))
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                modifier = Modifier
                    .height(80.dp)
                    .weight(1f),
                colors = CardDefaults.elevatedCardColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.TopEnd),
                        imageVector = Icons.Default.PeopleAlt,
                        contentDescription = "marker",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(8.dp)
                    ) {
                        Text("8", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Friends", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp)
        ) {
            Text("Bio", fontWeight = FontWeight.Bold)
            Text(state.bio, fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light)
        }
        Spacer(Modifier.height(16.dp))
    }
}


@Composable
@Preview(showSystemUi = true)
fun ProfileScreenPreview(modifier: Modifier = Modifier) {
    AppTheme(
        darkTheme = false
    ) {
        ProfileContent(
            state = ProfileState(),
            onMarkersClick = {  },
        )
    }
}