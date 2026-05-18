@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shareyourvoicemapbox.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.domain.amplituda.ParseAmplitudesUseCase
import com.example.shareyourvoicemapbox.ui.components.MarkerCard
import com.example.shareyourvoicemapbox.ui.navigation.Route
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute
import com.example.shareyourvoicemapbox.ui.screens.edit.PlayerState
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel<FeedViewModel>(),
    navHostController: NavHostController
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val playerState by viewModel.playerState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { state.markers.size })
    val refreshState = rememberPullToRefreshState()

    val waveformProgress by remember {
        derivedStateOf {
            (playerState.currentPosition / playerState.maxDuration.toFloat())
                .coerceIn(0f, 1f)
        }
    }
    LaunchedEffect(pagerState) {
        var first = true

        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                if (first) {
                    first = false
                    return@collect
                }
                state.markers.getOrNull(page)?.let {
                    scope.launch {
                        viewModel.playAudio(it.audioUrl, page)
                    }
                }
            }
    }

    LaunchedEffect(state.isViewingPublic) {
        viewModel.pauseAudio()
        if (state.isViewingPublic) {
            viewModel.getData()
        }
        else {
            viewModel.getFriendsMarker()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.pauseAudio()
        }
    }

    FeedContent(
        state = state,
        playerState = playerState,
        pagerState = pagerState,
        refreshState = refreshState,
        onViewPublicClick = {
            viewModel.viewPublic()
        },
        onViewFriendsClick = {
            viewModel.viewFriends()
        },
        onSearchClick = {

        },
        onRefresh = {
            viewModel.getData()
        },
        onPlayClick = { url, id ->
            if (playerState.isPlaying && url == state.currentAudioUrl) {
                viewModel.pauseAudio()
            }
            else {
                if (url != state.currentAudioUrl) {
                    scope.launch {
                        viewModel.getAudioDurationMs(url)
                        viewModel.playAudio(url, id)
                    }
                }
                else {
                    viewModel.playAudio(url, id)
                }
            }
        },
        onOpenMap = { markerId ->
            navHostController.navigate("${Route.MAP.route}?markerId=${markerId}")
        },
        onWaveformProgressChange = { progress ->
            viewModel.onWaveformProgressChange(progress)
        },
        progress = waveformProgress,
        onNameClick = { username ->
            navHostController.navigate("${SecondaryRoute.PERSON.route}?username=$username")
        }
    )
}

@Composable
fun FeedContent(
    modifier: Modifier = Modifier,
    state: FeedState,
    playerState: PlayerState,
    pagerState: PagerState,
    refreshState: PullToRefreshState,
    onViewPublicClick: () -> Unit,
    onViewFriendsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRefresh: () -> Unit,
    onPlayClick: (String, Int) -> Unit,
    onOpenMap: (Long) -> Unit,
    onWaveformProgressChange: (Float) -> Unit,
    progress: Float,
    onNameClick: (String) -> Unit,
) {
    PullToRefreshBox(
        state = refreshState,
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
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
        Column(
            modifier = Modifier.statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp, 8.dp, 24.dp, 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Discover",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                IconButton(
                    onClick = onSearchClick,
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            PrimaryTabRow(
                selectedTabIndex = if (state.isViewingPublic) 0 else 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp),
            ) {
                Tab(
                    selected = state.isViewingPublic,
                    onClick = onViewPublicClick,
                ) {
                    Text(
                        text = "Global",
                        color = if (state.isViewingPublic) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Tab(
                    selected = !state.isViewingPublic,
                    onClick = onViewFriendsClick,
                ) {
                    Text(
                        text = "Friends",
                        color = if (!state.isViewingPublic) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            VerticalPager(
                state = pagerState,
                contentPadding = PaddingValues(0.dp, 16.dp, 0.dp, 0.dp),
                beyondViewportPageCount = 1,
                pageSize = PageSize.Fixed(500.dp),
            ) { page ->
                val marker = state.markers[page]
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    MarkerCard(
                        modifier = Modifier
                            .padding(
                                24.dp,
                                0.dp,
                            )
                            .graphicsLayer {
                                val pageOffset = (
                                        (pagerState.currentPage - page) + pagerState
                                            .currentPageOffsetFraction
                                        ).absoluteValue

                                scaleX = lerp(
                                    start = 0.95f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f),
                                )
                                scaleY = lerp(
                                    start = 0.95f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f),
                                )
                            },
                        title = marker.title,
                        location = marker.location,
                        username = marker.authorUsername,
                        avatarUrl = marker.authorAvatarUrl,
                        imageUrl = marker.imageUrl ?: "",
                        onPlayClick = { onPlayClick(marker.audioUrl, page) },
                        onOpenMap = {
                            onOpenMap(marker.id)
                        },
                        amplitudes = ParseAmplitudesUseCase().invoke(marker.amplitudes),
                        waveformProgress = if (state.currentAudioUrl == marker.audioUrl) progress else 0f,
                        onWaveformProgressChange = onWaveformProgressChange,
                        name = marker.authorName,
                        createdAt = marker.createdAt,
                        audioUrl = marker.audioUrl,
                        isPLaying = playerState.isPlaying && state.currentAudioUrl == marker.audioUrl,
                        onMenuClick = {},
                        onNameClick = {
                            onNameClick(marker.authorUsername)
                        }
                    )
                }
            }
            Spacer(Modifier.height(140.dp))
            Text("No markers yet")
        }
    }
}