package com.example.shareyourvoicemapbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.shareyourvoicemapbox.data.MarkerRepository
import com.example.shareyourvoicemapbox.data.recorder.AudioRecorderImpl
import com.example.shareyourvoicemapbox.domain.recorder.StartRecordingUseCase
import com.example.shareyourvoicemapbox.domain.recorder.StopRecordingUseCase
import com.example.shareyourvoicemapbox.data.source.MarkerDataSource
import com.example.shareyourvoicemapbox.domain.markers.CreateMarkerUseCase
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersUseCase
import com.example.shareyourvoicemapbox.ui.navigation.AppNav
import com.example.shareyourvoicemapbox.ui.screens.map.MapViewModel
import com.example.shareyourvoicemapbox.ui.screens.map.MapViewModelFactory
import com.example.shareyourvoicemapbox.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recorder = AudioRecorderImpl(applicationContext)

        val startRecordingUseCase = StartRecordingUseCase(recorder)
        val stopRecordingUseCase = StopRecordingUseCase(recorder)

        val markerRepository = MarkerRepository(MarkerDataSource())

        val getMarkersUseCase = GetMarkersUseCase(
            markerRepository = markerRepository
        )

        val createMarkerUseCase = CreateMarkerUseCase(
            markerRepository = markerRepository
        )

        val factory = MapViewModelFactory(
            getMarkersUseCase,
            createMarkerUseCase,
            startRecordingUseCase,
            stopRecordingUseCase
        )

        val mapViewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]
        setContent {
            AppTheme {
                AppNav(viewModel = mapViewModel)
            }
        }
    }
}