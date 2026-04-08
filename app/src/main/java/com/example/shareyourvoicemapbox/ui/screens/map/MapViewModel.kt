package com.example.shareyourvoicemapbox.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.data.dto.CreateMarkerDTO
import com.example.shareyourvoicemapbox.domain.recorder.StartRecordingUseCase
import com.example.shareyourvoicemapbox.domain.recorder.StopRecordingUseCase
import com.example.shareyourvoicemapbox.domain.markers.CreateMarkerUseCase
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val getMarkersUseCase: GetMarkersUseCase,
    private val createMarkerUseCase: CreateMarkerUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase
) : ViewModel() {



    private val _uiState: MutableStateFlow<MapState> = MutableStateFlow(MapState.Content())
    val uiState = _uiState.asStateFlow()

    init {
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            getMarkersUseCase().fold(
                onSuccess = { data ->
                    val current = _uiState.value
                    if (current is MapState.Content) {
                        _uiState.value = current.copy(
                            markers = data,
                            error = ""
                        )
                    }
                },
                onFailure = { error ->
                    val current = _uiState.value
                    if (current is MapState.Content) {
                        _uiState.value = current.copy(
                            error = error.message ?: ""
                        )
                    }
                }
            )
        }
    }

    fun createMarker(dto: CreateMarkerDTO) {
        viewModelScope.launch {
            createMarkerUseCase(dto).fold(
                onSuccess = {
                    getData()
                },
                onFailure = {

                }
            )
        }
    }
    fun startRecording() {
        startRecordingUseCase()
        val current = _uiState.value
        if (current is MapState.Content) {
            _uiState.value = current.copy(
                isRecording = true
            )
        }
    }

    fun stopRecording() {
        val path = stopRecordingUseCase()
        val current = _uiState.value
        if (current is MapState.Content) {
            _uiState.value = current.copy(
                isRecording = false
            )
        }
    }

    fun openAddMarkerDialog() {
        val currentState = _uiState.value

        if (currentState is MapState.Content) {
            _uiState.value = currentState.copy(
                showAddMarkerDialog = true
            )
        }
    }
    fun closeAddMarkerDialog() {
        val currentState = _uiState.value

        if (currentState is MapState.Content) {
            _uiState.value = currentState.copy(
                showAddMarkerDialog = false
            )
        }
    }
    fun openPermissionSettingsDialog() {
        val currentState = _uiState.value

        if (currentState is MapState.Content) {
            _uiState.value = currentState.copy(
                showMicPermissionDialog = true
            )
        }
    }
    fun closePermissionSettingsDialog() {
        val currentState = _uiState.value

        if (currentState is MapState.Content) {
            _uiState.value = currentState.copy(
                showMicPermissionDialog = false
            )
        }
    }

}