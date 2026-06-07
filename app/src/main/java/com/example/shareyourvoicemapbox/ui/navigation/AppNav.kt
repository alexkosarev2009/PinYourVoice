package com.example.shareyourvoicemapbox.ui.navigation

import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import com.example.shareyourvoicemapbox.ui.screens.friends.FriendsScreen
import com.example.shareyourvoicemapbox.ui.screens.invitation.InvitationScreen
import com.example.shareyourvoicemapbox.ui.screens.map.MapScreen
import com.example.shareyourvoicemapbox.ui.screens.person.PersonScreen
import com.example.shareyourvoicemapbox.ui.screens.profile.ProfileScreen
import com.example.shareyourvoicemapbox.ui.screens.report.ReportScreen
import com.example.shareyourvoicemapbox.ui.screens.search.SearchScreen

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

        composable(
            "${Route.MAP.route}?markerId={markerId}",
            arguments = listOf(
                navArgument("markerId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            MapScreen(modifier, navHostController)
        }
        composable(Route.FEED.route) {
            FeedScreen(modifier, navHostController = navHostController)
        }
        composable(Route.PROFILE.route) {
            ProfileScreen(modifier, navHostController)
        }
        composable(SecondaryRoute.SEARCH.route) {
            SearchScreen(modifier, navHostController)
        }
        composable("${SecondaryRoute.FRIENDS.route}?userId={userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            )) {
            FriendsScreen(modifier, navHostController)
        }

        composable(
            "${SecondaryRoute.EDIT.route}/{audioPath}?lat={lat}&lng={lng}",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500),
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(500),
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(500),
                )
            },
            arguments = listOf(
                navArgument("audioPath") { type = NavType.StringType },
                navArgument("lat") {
                    type = NavType.StringType
                    defaultValue = "0.0"
                },
                navArgument("lng") {
                    type = NavType.StringType
                    defaultValue = "0.0"
                },
            ),
        ) {
            EditScreen(navHostController = navHostController, modifier = modifier)
        }
        composable(SecondaryRoute.AUTH.route) {
            AuthScreen(navHostController = navHostController)
        }
        composable(SecondaryRoute.INVITATIONS.route) {
            InvitationScreen(modifier, navHostController)
        }
        composable(
            "${SecondaryRoute.PERSON.route}?username={username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            PersonScreen(modifier, navHostController)
        }
        composable(
            "${SecondaryRoute.REPORT.route}/{markerId}",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500),
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500),
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(500),
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(500),
                )
            },
            arguments = listOf(
                navArgument("markerId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            ReportScreen(modifier, navHostController)
        }
    }
}

@Composable
fun AppNav(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = hiltViewModel<AppViewModel>(),
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    val startDestination = viewModel.startDestination

    val navController = rememberNavController()

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(SecondaryRoute.AUTH.route) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = remember { Route.entries.map { it.route } }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.navigationBars,

        bottomBar = {
            Log.d("Route", currentRoute.toString())
            if (currentRoute in mainRoutes || currentRoute == "map?markerId={markerId}") {

                NavigationBar(
                    containerColor =
                        if (currentRoute == "map?markerId={markerId}") Color.Transparent
                        else NavigationBarDefaults.containerColor,
                    modifier = if (currentRoute == "map?markerId={markerId}") Modifier.background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A1A2F),
                                Color(0xFF0B1020),
                                Color(0xFF05060A),
                            ),
                        ),
                    )
                    else Modifier,
                ) {
                    if (currentRoute == "map?markerId={markerId}") Log.d("BAR", "YES")
                    Route.entries.forEach { route ->

                        NavigationBarItem(
                            colors = if (currentRoute == "map?markerId={markerId}") NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            ) else NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            selected = when (route) {
                                Route.MAP -> currentRoute == "map?markerId={markerId}"
                                else -> currentRoute == route.route
                            },
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
                                    imageVector = when (route) {
                                        Route.MAP -> if (currentRoute == "map?markerId={markerId}") route.icon else route.iconOutlined
                                        else -> if (route.route == currentRoute) route.icon else route.iconOutlined
                                    },
                                    contentDescription = stringResource(route.contentDescription),
                                    modifier = Modifier.size(28.dp),
                                )
                            },
                            label = {
                                Text(stringResource(route.contentDescription))
                            },
                        )
                    }
                }
            }
        },
    ) { padding ->

        AppNavHost(
            navHostController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
        )
    }
}
