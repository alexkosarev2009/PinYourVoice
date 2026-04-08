package com.example.shareyourvoicemapbox.ui.navigation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shareyourvoicemapbox.ui.screens.auth.AuthScreen
import com.example.shareyourvoicemapbox.ui.screens.feed.FeedScreen
import com.example.shareyourvoicemapbox.ui.screens.map.MapScreen
import com.example.shareyourvoicemapbox.ui.screens.map.MapViewModel
import com.example.shareyourvoicemapbox.ui.screens.profile.ProfileScreen
import com.example.shareyourvoicemapbox.ui.screens.register.RegisterScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun AppNavHost(
    navHostController: NavHostController,
    startDestination: String,
    modifier: Modifier,
    viewModel: MapViewModel,
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
                    Route.MAP -> MapScreen(modifier, viewModel = viewModel)
                    Route.FEED -> FeedScreen(modifier)
                    Route.PROFILE -> ProfileScreen(modifier)
                }
            }
        }
        AuthRoute.entries.forEach { route ->
            composable(route.route) {
                when (route) {
                    AuthRoute.AUTH -> AuthScreen(navHostController)
                    AuthRoute.REGISTER -> RegisterScreen()
                }
            }
        }
    }
}
@Composable
fun AppNav(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel,
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
            startDestination = AuthRoute.AUTH.route,
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
        )
    }
}