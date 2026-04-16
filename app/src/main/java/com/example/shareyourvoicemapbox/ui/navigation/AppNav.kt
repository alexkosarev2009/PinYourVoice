package com.example.shareyourvoicemapbox.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shareyourvoicemapbox.ui.screens.auth.AuthScreen
import com.example.shareyourvoicemapbox.ui.screens.edit.EditScreen
import com.example.shareyourvoicemapbox.ui.screens.feed.FeedScreen
import com.example.shareyourvoicemapbox.ui.screens.map.MapScreen
import com.example.shareyourvoicemapbox.ui.screens.profile.ProfileScreen
import com.example.shareyourvoicemapbox.ui.screens.register.RegisterScreen

@Composable
fun AppNavHost(
    navHostController: NavHostController,
    startDestination: String,
    modifier: Modifier,
) {

    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        exitTransition = {
            fadeOut(animationSpec = tween(0))
        },
        enterTransition = {
            fadeIn(animationSpec = tween(200))
        },

        ) {
        Route.entries.forEach { route ->
            composable(route.route) {
                when (route) {
                    Route.MAP -> MapScreen(modifier, navHostController)
                    Route.FEED -> FeedScreen(modifier)
                    Route.PROFILE -> ProfileScreen(modifier)
                }
            }
        }
        composable("${SecondaryRoute.EDIT.route}/{audioPath}?lat={lat}&lng={lng}",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(500)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(500)
                )
            },
            arguments = listOf(
                navArgument("audioPath") {type = NavType.StringType},
                navArgument("lat") {
                    type = NavType.StringType
                    defaultValue = "0.0"
                },
                navArgument("lng") {
                    type = NavType.StringType
                    defaultValue = "0.0"
                }
            )
        ) {
            EditScreen(navHostController = navHostController, modifier = modifier)
        }
    }
}
@Composable
fun AppNav(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = remember { Route.entries.map { it.route } }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.navigationBars,

        bottomBar = {
            if (currentRoute in mainRoutes) {
                NavigationBar {
                    Route.entries.forEach { route ->
                        NavigationBarItem(
                            selected = currentRoute == route.route,
                            onClick = {
                                navController.navigate(route.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(Route.MAP.route) {
                                        saveState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == route.route)
                                        route.icon
                                    else route.iconOutlined,
                                    contentDescription = route.contentDescription,
                                    modifier = Modifier.size(28.dp),
                                )
                            },
                            label = {
                                Text(route.contentDescription)
                            },
                        )
                    }
                }
            }
        }
        ) { padding ->

        AppNavHost(
            navHostController = navController,
            startDestination = Route.MAP.route,
            modifier = Modifier.padding(padding),
        )
    }
}