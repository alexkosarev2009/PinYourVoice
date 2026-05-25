package com.example.shareyourvoicemapbox.ui.navigation

import androidx.lifecycle.ViewModel
import com.example.shareyourvoicemapbox.data.source.auth.bearer.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenStorage: TokenStorage
): ViewModel() {
    val startDestination = if (
        tokenStorage.get() != null &&
        !tokenStorage.isTokenExpired()
    ) {
        Route.MAP.route
    } else {
        SecondaryRoute.AUTH.route
    }
}