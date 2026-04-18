package com.example.shareyourvoicemapbox.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.example.shareyourvoicemapbox.domain.auth.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase
): ViewModel() {

    fun onLogOutClick() {
        logOutUseCase()
    }
}