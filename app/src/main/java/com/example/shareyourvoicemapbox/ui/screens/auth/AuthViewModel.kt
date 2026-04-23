package com.example.shareyourvoicemapbox.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.data.dto.UserLoginDTO
import com.example.shareyourvoicemapbox.domain.auth.CheckAndSaveAuthUseCase
import com.example.shareyourvoicemapbox.domain.auth.CheckAuthFormatUseCase
import com.example.shareyourvoicemapbox.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkAndSaveAuthUseCase: CheckAndSaveAuthUseCase,
) : ViewModel() {
    private val checkAuthFormatUseCase: CheckAuthFormatUseCase =
        CheckAuthFormatUseCase()
    private val _uiState = MutableStateFlow(AuthState.Content())
    val uiState = _uiState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<AuthAction>()
    val actionFlow = _actionFlow.asSharedFlow()

    fun onChangeInput(
        login: String,
        password: String
    ) {
        _uiState.update {
            it.copy(
                username = login,
                password = password,
                error = "",
                isEnableLogin = checkAuthFormatUseCase(login, password)
            )
        }
    }
    fun onLoginClick(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            checkAndSaveAuthUseCase.invoke(UserLoginDTO(
                username = username,
                password = password
            )).fold(
                onFailure = { error ->
                    _uiState.emit(AuthState.Content(
                        username = username,
                        error = error.message ?: ""
                    ))
                },
                onSuccess = {
                    _actionFlow.emit(AuthAction.OpenScreen(Route.MAP.route))
                }
            )
        }
    }

    fun onSignInClick() {
        if (!_uiState.value.isSignInSelected) {
            _uiState.update {
                it.copy(isSignInSelected = true,
                    isSignUpSelected = false)
            }
        }
    }
    fun onSignUpClick() {
        if (!_uiState.value.isSignUpSelected) {
            _uiState.update {
                it.copy(isSignUpSelected = true,
                    isSignInSelected = false)
            }
        }
    }
}