package com.example.shareyourvoicemapbox.ui.screens.edit

data class EditState(
    val audioPath: String = "",
    val amplitudes: List<Int> = emptyList(),
    val error: String = ""
)