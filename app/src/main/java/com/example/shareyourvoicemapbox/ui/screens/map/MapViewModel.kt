package com.example.shareyourvoicemapbox.ui.screens.map

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersUseCase
import com.example.shareyourvoicemapbox.domain.network.NetworkMonitor
import com.example.shareyourvoicemapbox.domain.recorder.ReleaseRecorderUseCase
import com.example.shareyourvoicemapbox.domain.recorder.StartRecordingUseCase
import com.example.shareyourvoicemapbox.domain.recorder.StopRecordingUseCase
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getMarkersUseCase: GetMarkersUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val releaseRecorderUseCase: ReleaseRecorderUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState: MutableStateFlow<MapState> = MutableStateFlow(MapState.Content())
    val uiState = _uiState.asStateFlow()
    private val _systemState = MutableStateFlow(MapSystemState())
    val systemState = _systemState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<MapAction>()
    val actionFlow = _actionFlow.asSharedFlow()

    val isConnected = networkMonitor.observe()
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            networkMonitor.isCurrentlyConnected())

    private var hasCenteredUser = false

    private val _currentMarker = MutableStateFlow<MarkerEntity?>(null)
    val currentMarker = _currentMarker.asStateFlow()

    private var timerJob: Job? = null
    val minDuration = 3_000L
    val maxDuration = 30_000L

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


    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            while (isActive) {
                val time = System.currentTimeMillis() - startTime

                _systemState.update {
                    it.copy(recordTimeMs = time)
                }

                if (time >= maxDuration) {
                    stopRecording()
                    break
                }

                delay(50)
            }
        }
    }
    fun startRecording() {
        startRecordingUseCase()
        _systemState.update {
            it.copy(isRecording = true,
                recordTimeMs = 0L)
        }
        startTimer()
    }

    fun stopRecording() {
        timerJob?.cancel()
        timerJob = null

        val duration = _systemState.value.recordTimeMs
        val path = stopRecordingUseCase()
        val isValid = duration >= minDuration

        _systemState.update {
            it.copy(
                isRecording = false,
                currentAudioPath = if (isValid) path else null,
                recordTimeMs = duration
            )
        }
        if (isValid) {
            savedStateHandle["audioPath"] = path
        }
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
        _uiState.update { state ->
            if (state is MapState.Content) {
                state.copy(showAddMarkerDialog = true)
            }
            else state
        }
    }
    fun closeAddMarkerDialog() {
        _uiState.update { state ->
            if (state is MapState.Content) {
                state.copy(showAddMarkerDialog = false,)
            }
            else state
        }
        _systemState.update { state ->
            state.copy(currentAudioPath = null)
        }
    }
    fun openMicPermissionDialog() {
        _uiState.update { state ->
            if (state is MapState.Content) {
                state.copy(showMicPermissionDialog = true)
            }
            else state
        }
    }
    fun closeMicPermissionDialog() {
        _uiState.update { state ->
            if (state is MapState.Content) {
                state.copy(showMicPermissionDialog = false)
            }
            else state
        }
    }

    fun openFineLocationPermissionDialog() {
        _uiState.update { state ->
            if (state is MapState.Content) {
                state.copy(showFineLocationPermissionDialog = true)
            }
            else state
        }
    }
    fun closeFineLocationPermissionDialog() {
        _uiState.update { state ->
            if (state is MapState.Content) {
                state.copy(showFineLocationPermissionDialog = false)
            }
            else state
        }
    }

    fun updateUserLocation(point: Point) {
        _systemState.update {
            it.copy(userLocation = point)
        }
        if (!hasCenteredUser) {
            hasCenteredUser = true
            _systemState.update {
                it.copy(hasCenteredUser = true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        releaseRecorderUseCase()
    }
    fun onSaveRecordingClick() {
        viewModelScope.launch {
            _actionFlow.emit(MapAction.OpenScreen(SecondaryRoute.EDIT.route))
        }
    }
    fun onDeleteRecordingClick() {
        _systemState.update {
            it.copy(
                currentAudioPath = null,
                recordTimeMs = 0L
            )
        }
        savedStateHandle["audioPath"] = null
    }

    fun openViewMarkerDialog(marker: MarkerEntity) {
        viewModelScope.launch {
            _currentMarker.emit(marker)
        }
        _uiState.update { state ->
            if (state is MapState.Content) {
                state.copy(
                    showViewMarkerDialog = true,
                )
            }
            else state
        }
    }

    fun closeViewMarkerDialog() {
        viewModelScope.launch {
            _currentMarker.emit(null)
        }
        _uiState.update { state ->
            if (state is MapState.Content) {
                state.copy(
                    showViewMarkerDialog = false,
                )
            }
            else state
        }
    }

}