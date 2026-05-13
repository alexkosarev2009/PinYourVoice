package com.example.shareyourvoicemapbox.ui.screens.auth

sealed interface AuthState {
    data class Content(
        val username: String = "",
        val password: String = "",

        val email: String = "",
        val registerPassword: String = "",
        val registerUsername: String = "",
        val registerName: String = "",
        val isEnableRegister: Boolean = false,
        val registerStep: Int = 1,

        val error: String = "",
        val isEnableLogin: Boolean = false,
        val isSignInSelected: Boolean = true,
        val isSignUpSelected: Boolean = false,
        )
}