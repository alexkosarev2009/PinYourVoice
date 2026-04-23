package com.example.shareyourvoicemapbox.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.auth.LogOutUseCase
import com.example.shareyourvoicemapbox.domain.entities.UserEntity
import com.example.shareyourvoicemapbox.domain.users.GetMeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase,
    private val getMeUseCase: GetMeUseCase,
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    fun onLogOutClick() {
        logOutUseCase()
    }

    init {
        loadUserInfo()
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            getMeUseCase().fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            fullName = user.name,
                            userName = user.username,
                            bio = user.bio ?: "Empty bio",
                            avatarUrl = user.avatarUrl ?: ""
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "")
                    }
                }
            )
        }
    }
    fun onMenuClick() {
        logOutUseCase()
    }
}