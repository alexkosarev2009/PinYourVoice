package com.example.shareyourvoicemapbox.ui.screens.map

sealed interface MapAction {
    data class OpenScreen(val route: String) : MapAction
}