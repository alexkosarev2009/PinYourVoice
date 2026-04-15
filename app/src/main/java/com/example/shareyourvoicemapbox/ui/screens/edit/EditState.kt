package com.example.shareyourvoicemapbox.ui.screens.edit

import android.net.Uri

data class EditState(
    val audioPath: String = "",
    val imageUri: Uri? = null,
    val amplitudes: List<Int> = emptyList(),
    val error: String = "",
    val title: String = "",
)