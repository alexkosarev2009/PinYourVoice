package com.example.shareyourvoicemapbox.ui.screens.auth

sealed interface AuthState {
    data class Content(
        val login: String = "",
        val password: String = "",
        val error: String = "",
        val isEnableLogin: Boolean = false,
        )
}