package com.example.shareyourvoicemapbox.ui.screens.feed

import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity

data class FeedState(
    val markers: List<MarkerEntity> = emptyList(),
    val friendsMarkers: List<MarkerEntity> = emptyList(),
    val error: String = "",
    val isViewingPublic: Boolean = true,
    val isRefreshing: Boolean = false,
)