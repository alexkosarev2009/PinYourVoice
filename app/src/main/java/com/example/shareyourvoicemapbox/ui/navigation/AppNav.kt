package com.example.shareyourvoicemapbox.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shareyourvoicemapbox.ui.screens.auth.AuthScreen
import com.example.shareyourvoicemapbox.ui.screens.feed.FeedScreen
import com.example.shareyourvoicemapbox.ui.screens.map.MapScreen
import com.example.shareyourvoicemapbox.ui.screens.profile.ProfileScreen
import com.example.shareyourvoicemapbox.ui.screens.register.RegisterScreen
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

@Composable
fun AppNavHost(
    navHostController: NavHostController,
    startDestination: Route,
    modifier: Modifier,
) {

    NavHost(
        navController = navHostController,
        startDestination = startDestination.route,
        exitTransition = {
            fadeOut(animationSpec = tween(0))
        },
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        }

    ) {
        Route.entries.forEach { route ->
            composable(route.route) {
                when (route) {
                    Route.MAP -> MapScreen(modifier)
                    Route.FEED -> FeedScreen(modifier)
                    Route.PROFILE -> ProfileScreen(modifier)
                }
            }
        }
    }
}

@Composable
fun NavigationBarExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Route.MAP
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Route.entries.forEachIndexed { index, route ->

                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = route.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                            selectedDestination = index

                        },
                        icon = {
                            Icon(
                                route.icon,
                                contentDescription = route.contentDescription
                            )
                        },
                        label = { Text(route.contentDescription) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedDestination == 1) {
                FloatingActionButton(
                    onClick = {},
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add marker",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

        }
    ) { contentPadding ->
//        MapboxMap(
//            modifier = Modifier.padding(contentPadding),
//            scaleBar = {}
//        ) {
//
//        }
        AppNavHost(navController, startDestination, Modifier.padding(contentPadding))
    }
}