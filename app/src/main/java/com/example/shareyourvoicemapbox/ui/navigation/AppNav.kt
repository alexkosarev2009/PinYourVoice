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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AppNav(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = remember { Route.entries.map { it.route } }

    var showAddMarkerDialog by rememberSaveable { mutableStateOf(false) }
    var title by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    var showMicPermissionDialog by rememberSaveable { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {
        }
    }
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)


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
        },

        floatingActionButton = {

            if (currentRoute == Route.MAP.route) {

                FloatingActionButton(
                    onClick = {
                        when {
                            permissionState.status.isGranted -> {
                                showAddMarkerDialog = true
                            }
                            permissionState.status.shouldShowRationale -> {
                                showMicPermissionDialog = true
                            }
                            else -> {
                                launcher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocationAlt,
                        contentDescription = "Add marker",
                        modifier = Modifier.size(30.dp),
                    )
                }
                if (showMicPermissionDialog) {
                    PermissionSettingsDialog(
                        onConfirm = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                            showMicPermissionDialog = false
                        },
                        onDismiss = {
                            showMicPermissionDialog = false
                        }
                    )
                }
            }
        },

        ) { padding ->

        AppNavHost(
            navHostController = navController,
            startDestination = AuthRoute.AUTH.route,
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
        )
    }

    if (showAddMarkerDialog) {
        ModalBottomSheet(
            onDismissRequest = { showAddMarkerDialog = false },
            scrimColor = Color.Transparent,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    shape = RoundedCornerShape(16.dp),
                    label = { Text("Enter title") },
                    modifier = Modifier.width(240.dp),
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun DialogPreview(modifier: Modifier = Modifier) {
    ModalBottomSheet(
        onDismissRequest = {
        },
        scrimColor = Color.Transparent,
    ) {
        Column(
            modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = "",
                shape = RoundedCornerShape(16.dp),
                onValueChange = {
                },
                label = {
                    Text("Title")
                },
                modifier = Modifier.width(320.dp),

                )
            Spacer(Modifier.height(16.dp))

        }
    }
}

@Composable
fun PermissionSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Couldn't record audio")
        },
        text = {
            Text(
                "Please allow microphone access in App Settings."            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("To settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}