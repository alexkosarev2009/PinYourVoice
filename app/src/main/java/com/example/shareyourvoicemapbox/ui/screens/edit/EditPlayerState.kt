package com.example.shareyourvoicemapbox.ui.screens.edit

data class EditPlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val maxDuration: Long = 0L
)