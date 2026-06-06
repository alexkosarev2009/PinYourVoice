package com.example.shareyourvoicemapbox.domain.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun logout() {
        _isLoggedIn.value = false
    }

    fun logIn() {
        _isLoggedIn.value = true
    }
}