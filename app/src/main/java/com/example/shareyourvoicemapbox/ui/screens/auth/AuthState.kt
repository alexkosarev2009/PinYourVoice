package com.example.shareyourvoicemapbox.ui.screens.auth

sealed interface AuthState {
    data class Content(
        val username: String = "",
        val password: String = "",
        val error: String = "",
        val isEnableLogin: Boolean = false,
        )
}