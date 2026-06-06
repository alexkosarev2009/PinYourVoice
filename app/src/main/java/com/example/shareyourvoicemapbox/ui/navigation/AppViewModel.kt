package com.example.shareyourvoicemapbox.ui.navigation

import androidx.lifecycle.ViewModel
import com.example.shareyourvoicemapbox.data.source.auth.storage.TokenStorage
import com.example.shareyourvoicemapbox.domain.auth.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
): ViewModel() {

    val isLoggedIn = sessionManager.isLoggedIn
    val startDestination = if (
        isLoggedIn.value
    ) {
        Route.MAP.route
    } else {
        SecondaryRoute.AUTH.route
    }
}