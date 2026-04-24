package com.example.shareyourvoicemapbox.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
                    Route.PROFILE -> ProfileScreen(modifier, navHostController = navHostController)
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
        composable(SecondaryRoute.AUTH.route) {
            AuthScreen(navHostController = navHostController)
        }
        composable(SecondaryRoute.REGISTER.route) {
            RegisterScreen()
        }
    }
}
@Composable
fun AppNav(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel<AppViewModel>()
) {
    val startDestination = viewModel.startDestination

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = remember { Route.entries.map { it.route } }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.navigationBars,

        bottomBar = {
            if (currentRoute in mainRoutes) {
                NavigationBar(
                    containerColor =
                        if (isMapRoute(currentRoute ?: "")) Color.Transparent
                        else NavigationBarDefaults.containerColor,
                    modifier = if (isMapRoute(currentRoute ?: "")) Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A1A2F),
                                Color(0xFF0B1020),
                                Color(0xFF05060A),
                            )
                        )
                    )
                    else Modifier
                ) {
                    Route.entries.forEach { route ->
                        NavigationBarItem(
                            colors = if (isMapRoute(currentRoute ?: "")) NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f)
                            ) else NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            ),
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
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
        )
    }
}

fun isMapRoute(route: String): Boolean {
    return route == Route.MAP.route
}