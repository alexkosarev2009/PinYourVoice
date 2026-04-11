package com.example.shareyourvoicemapbox.ui.screens.auth

sealed interface AuthAction {
    data class OpenScreen(val route: String) : AuthAction
}