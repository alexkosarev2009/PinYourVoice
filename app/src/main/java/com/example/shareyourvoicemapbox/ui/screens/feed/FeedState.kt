package com.example.shareyourvoicemapbox.ui.screens.feed

import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity

data class FeedState(
    val markers: List<MarkerEntity> = emptyList(),
    val error: String = ""
)