package com.example.shareyourvoicemapbox.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shareyourvoicemapbox.domain.recorder.StartRecordingUseCase
import com.example.shareyourvoicemapbox.domain.recorder.StopRecordingUseCase
import com.example.shareyourvoicemapbox.domain.markers.CreateMarkerUseCase
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersUseCase

class MapViewModelFactory(
    private val getMarkersUseCase: GetMarkersUseCase,
    private val createMarkerUseCase: CreateMarkerUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(
                getMarkersUseCase,
                createMarkerUseCase,
                startRecordingUseCase,
                stopRecordingUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}