package com.example.shareyourvoicemapbox.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.data.MarkerRepository
import com.example.shareyourvoicemapbox.data.source.MarkerDataSource
import com.example.shareyourvoicemapbox.domain.GetMarkersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val getMarkersUseCase = GetMarkersUseCase(
        markerRepository = MarkerRepository(MarkerDataSource())
    )
    private val _uiState: MutableStateFlow<MapState> = MutableStateFlow(MapState.Content())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    init {
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            getMarkersUseCase().fold(
                onSuccess = { data ->
                    _uiState.emit(MapState.Content(data))
                },
                onFailure = { error ->
                    _uiState.emit(MapState.Error(error.message.orEmpty()))
                }
            )
        }
    }
}