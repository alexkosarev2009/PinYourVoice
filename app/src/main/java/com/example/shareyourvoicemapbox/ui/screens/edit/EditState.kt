package com.example.shareyourvoicemapbox.ui.screens.edit

import android.net.Uri

data class EditState(
    val audioPath: String = "",
    val imagePath: String = "",
    val imageUri: Uri? = null,
    val amplitudes: List<Int> = emptyList(),
    val error: String = "",
    val title: String = "",
    val isLoading: Boolean = false,
    val lng: Double = 0.0,
    val lat: Double = 0.0,
)