package com.example.shareyourvoicemapbox.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.data.repo.AuthRepository
import com.example.shareyourvoicemapbox.data.source.auth.AuthLocalDataSource
import com.example.shareyourvoicemapbox.data.source.auth.AuthNetworkDataSource
import com.example.shareyourvoicemapbox.domain.auth.CheckAndSaveAuthUseCase
import com.example.shareyourvoicemapbox.domain.auth.CheckAuthFormatUseCase
import com.example.shareyourvoicemapbox.ui.navigation.Route
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val checkAndSaveAuthUseCase by lazy {
        CheckAndSaveAuthUseCase(
            AuthRepository(
                networkDataSource = AuthNetworkDataSource(),
                localDataSource = AuthLocalDataSource
            )
        )
    }
    private val checkAuthFormatUseCase by lazy {
        CheckAuthFormatUseCase()
    }

    private val _uiState = MutableStateFlow(AuthState.Content())
    val uiState = _uiState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<AuthAction>()
    val actionFlow = _actionFlow.asSharedFlow()

    fun onChangeInput(
        login: String,
        password: String
    ) {
        viewModelScope.launch {
            _uiState.emit(AuthState.Content(
                login = login,
                password = password,
                error = "",
                isEnableLogin = checkAuthFormatUseCase.invoke(login, password)
                )
            )
        }
    }
    fun onLoginClick(
        login: String,
        password: String
    ) {
        viewModelScope.launch {
            checkAndSaveAuthUseCase.invoke(login, password).fold(
                onFailure = { error ->
                    _uiState.emit(AuthState.Content(
                        login = login,
                        error = error.message ?: ""
                    ))
                },
                onSuccess = {
                    _actionFlow.emit(AuthAction.OpenScreen(Route.MAP.route))
                }
            )
        }
    }
}