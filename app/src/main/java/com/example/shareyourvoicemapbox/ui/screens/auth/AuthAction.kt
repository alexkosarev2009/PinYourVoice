package com.example.shareyourvoicemapbox.ui.screens.auth

import com.example.shareyourvoicemapbox.ui.navigation.AuthRoute

sealed interface AuthAction {
    data class OpenScreen(val route: String) : AuthAction
}