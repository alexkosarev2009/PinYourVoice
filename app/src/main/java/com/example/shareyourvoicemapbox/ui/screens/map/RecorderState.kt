package com.example.shareyourvoicemapbox.ui.screens.map

data class RecorderState(
    val isRecording: Boolean = false,
    val progress: Float = 0f,
    val currentAudioPath: String = ""
)