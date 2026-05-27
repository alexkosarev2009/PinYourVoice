package com.example.shareyourvoicemapbox.ui.screens.report

data class ReportState(
    val markerId: Long = -1L,
    val selectedOption: String = "",
    val isLoading: Boolean = false,
    val isDone: Boolean = false,
    val error: String = "",
)