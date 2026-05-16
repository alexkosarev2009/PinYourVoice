@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shareyourvoicemapbox.ui.screens.person

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.example.shareyourvoicemapbox.ui.components.ShortMarkerCard
import com.example.shareyourvoicemapbox.ui.navigation.Route
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute
import com.example.shareyourvoicemapbox.ui.screens.profile.ProfileContent
import kotlinx.coroutines.launch

@Composable
fun PersonScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: PersonViewModel = hiltViewModel<PersonViewModel>()
) {
    val state by viewModel.state.collectAsState()

    val listState = rememberLazyListState()

    val refreshState = rememberPullToRefreshState()

    val scope = rememberCoroutineScope()

    val showScrollTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex >= 2
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getPersonInfo()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearUser()
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            viewModel.getPersonInfo()
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
                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Go back"
                        )
                    }
                    Row {
                        IconButton(
                            onClick = {

                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Notifications",
                            )
                        }

                        Spacer(Modifier.width(4.dp))

                        IconButton(
                            onClick = {
                                viewModel.onMenuClick()
                                navHostController.navigate(SecondaryRoute.AUTH.route) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
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
                ShortMarkerCard(
                    modifier = Modifier.padding(24.dp, 0.dp),
                    marker = marker,
                    onMenuClick = {},
                    waveformProgress = 0f,
                    onWaveformProgressChange = {},
                    onPlayClick = { },
                    isPLaying = false,
                    onOpenMap = {
                        navHostController.navigate("${Route.MAP.route}?markerId=${marker.id}")
                    }
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
            item {
                Spacer(Modifier.height(140.dp))
            }
        }
    }
}