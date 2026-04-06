package com.example.shareyourvoicemapbox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface c

enum class Route(
    val route: String,
    val icon: ImageVector,
    val iconOutlined: ImageVector,
    val contentDescription: String = ""
) {
    FEED("feed",
        Icons.AutoMirrored.Filled.Comment,
        Icons.AutoMirrored.Outlined.Comment,
        "Feed"),
    MAP("map",
        Icons.Filled.Explore,
        Icons.Outlined.Explore, "Map"),
    PROFILE("profile",
        Icons.Filled.Person,
        Icons.Outlined.Person,
        "Profile")
}

enum class AuthRoute(
    val route: String
) {
    AUTH("auth"),
    REGISTER("register")
}