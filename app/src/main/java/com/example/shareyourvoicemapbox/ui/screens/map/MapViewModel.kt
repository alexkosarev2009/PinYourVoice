package com.example.shareyourvoicemapbox.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.data.dto.CreateMarkerDTO
import com.example.shareyourvoicemapbox.domain.markers.CreateMarkerUseCase
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersUseCase
import com.example.shareyourvoicemapbox.domain.recorder.StartRecordingUseCase
import com.example.shareyourvoicemapbox.domain.recorder.StopRecordingUseCase
import com.mapbox.geojson.Point
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
    private val _systemState = MutableStateFlow(MapSystemState())
    val systemState = _systemState.asStateFlow()

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

    fun setLocationPermission(granted: Boolean) {
        _systemState.update {
            it.copy(hasLocationPermission = granted)
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
        _systemState.update {
            it.copy(isRecording = true)
        }
    }

    fun stopRecording(): String {
        val path = stopRecordingUseCase()
        _systemState.update {
            it.copy(
                isRecording = false,
                currentAudioPath = path
            )
        }
        return path
    }

    fun onRecordClick() {

        if (_systemState.value.isRecording) {
            stopRecording()
        }
        else startRecording()
    }
    fun onRecordRelease() {
        if (_systemState.value.isRecording) {
            stopRecording()
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

    fun updateUserLocation(point: Point) {
        _systemState.update {
            it.copy(userLocation = point)
        }
    }

}