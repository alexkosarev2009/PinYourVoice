package com.example.shareyourvoicemapbox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.shareyourvoicemapbox.R

enum class Route(
    val route: String,
    val icon: ImageVector,
    val iconOutlined: ImageVector,
    val contentDescription: Int,
) {
    FEED(
        "feed",
        Icons.AutoMirrored.Filled.Comment,
        Icons.AutoMirrored.Outlined.Comment,
        R.string.feed,
    ),
    MAP(
        "map",
        Icons.Filled.Explore,
        Icons.Outlined.Explore, R.string.map,
    ),
    PROFILE("profile",
        Icons.Filled.Person,
        Icons.Outlined.Person,
        R.string.profile)
}

enum class SecondaryRoute(
    val route: String
) {
    AUTH("auth"),
    EDIT("edit_marker"),
    PERSON("person"),
    INVITATIONS("invitations"),
    REPORT("report"),
    SEARCH("search"),
    FRIENDS("friends"),
    EDIT_PROFILE("edit_profile")
}