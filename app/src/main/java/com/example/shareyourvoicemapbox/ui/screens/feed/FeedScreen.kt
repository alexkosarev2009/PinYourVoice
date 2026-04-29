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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shareyourvoicemapbox.ui.components.MarkerCard
import com.example.shareyourvoicemapbox.ui.screens.edit.PlayerState
import kotlin.math.absoluteValue

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel<FeedViewModel>()
) {
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { state.markers.size })
    val refreshState = rememberPullToRefreshState()

    FeedContent(
        state = state,
        pagerState = pagerState,
        refreshState = refreshState,
        onViewPublicClick = {
            viewModel.viewPublic()
        },
        onViewFriendsClick = {
            viewModel.viewFriends()
        },
        onRefresh = {
            viewModel.getData()
        },
        onSearchClick = {

        }
    )
}

@Composable
fun FeedContent(
    modifier: Modifier = Modifier,
    state: FeedState,
    pagerState: PagerState,
    refreshState: PullToRefreshState,
    onViewPublicClick: () -> Unit,
    onViewFriendsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRefresh: () -> Unit
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
                        onPlayClick = {},
                        onOpenMap = {},
                        amplitudes = listOf(),
                        waveformProgress = 0f,
                        onWaveformProgressChange = {},
                        playerState = PlayerState(),
                        name = marker.authorName,
                        createdAt = marker.createdAt
                    )
                }
            }
        }
    }
}