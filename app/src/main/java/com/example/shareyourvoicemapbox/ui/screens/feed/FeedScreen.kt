package com.example.shareyourvoicemapbox.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
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

    FeedContent(
        state = state,
        pagerState = pagerState
    )
}

@Composable
fun FeedContent(
    modifier: Modifier = Modifier,
    state: FeedState,
    pagerState: PagerState
) {
    Column(
        modifier = Modifier.statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp, 8.dp, 24.dp, 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Discover",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }
        VerticalPager(
            state = pagerState,
            contentPadding = PaddingValues(0.dp, 96.dp, 0.dp, 0.dp),
            beyondViewportPageCount = 1,
            pageSize = PageSize.Fixed(500.dp),
        ) { page ->
            val marker = state.markers[page]
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MarkerCard(
                    modifier = Modifier.padding(
                        24.dp,
                        0.dp
                    ).graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        scaleX = lerp(
                            start = 0.95f,
                            stop = 1f,
                            amount = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleY = lerp(
                            start = 0.95f,
                            stop = 1f,
                            amount = 1f - pageOffset.coerceIn(0f, 1f)
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
                )
            }
        }
    }
}