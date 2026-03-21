package com.example.shareyourvoicemapbox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Route(
    val route: String,
    val icon: ImageVector,
    val contentDescription: String = ""
) {
    FEED("feed", Icons.AutoMirrored.Filled.Comment, "Feed"),
    MAP("map", Icons.Default.Map, "Map"),
    PROFILE("profile", Icons.Default.Person, "Profile")

}