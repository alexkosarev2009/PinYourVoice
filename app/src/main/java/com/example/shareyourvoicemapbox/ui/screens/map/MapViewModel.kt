package com.example.shareyourvoicemapbox.ui.screens.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapState.Content())
    val uiState = _uiState.asStateFlow()

}