package com.example.shareyourvoicemapbox.ui.screens.search

import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity

data class SearchState(
    val query: String = "",
    val markers: List<MarkerEntity> = emptyList(),
    val error: String = "",
)