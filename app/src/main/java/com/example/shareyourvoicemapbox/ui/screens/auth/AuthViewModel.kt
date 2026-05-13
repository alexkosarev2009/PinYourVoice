package com.example.shareyourvoicemapbox.ui.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.data.dto.UserLoginDTO
import com.example.shareyourvoicemapbox.data.dto.UserRegisterDTO
import com.example.shareyourvoicemapbox.domain.auth.CheckAndSaveAuthUseCase
import com.example.shareyourvoicemapbox.domain.auth.CheckAuthFormatUseCase
import com.example.shareyourvoicemapbox.domain.auth.RegisterUseCase
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
    private val registerUseCase: RegisterUseCase
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
                    _uiState.update {
                        it.copy(
                            username = username,
                            error = error.message ?: ""
                        )
                    }
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

    fun onChangeRegisterInput(email: String, registerPassword: String) {
        _uiState.update {
            it.copy(
                email = email,
                registerPassword = registerPassword,
                isEnableRegister = Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                        registerPassword.length >= 5
            )
        }
    }
    fun onRegisterClick() {
        _uiState.update {
            it.copy(
                registerStep = 2
            )
        }
    }

    fun goBack() {
        _uiState.update {
            it.copy(
                registerName = "",
                registerUsername = "",
                registerStep = 1
            )
        }
    }

    fun onChangeNameInput(username: String, name: String) {
        _uiState.update {
            it.copy(
                registerUsername = username,
                registerName = name
            )
        }
    }

    fun finishRegister() {
        viewModelScope.launch {
            registerUseCase(
                UserRegisterDTO(
                    name = _uiState.value.registerName,
                    username = _uiState.value.registerUsername,
                    email = _uiState.value.email,
                    password = _uiState.value.registerPassword,
                )
            ).fold(
                onSuccess = {
                    onLoginClick(
                        _uiState.value.registerUsername,
                        _uiState.value.registerPassword)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message ?: ""
                        )
                    }
                }
            )
        }
    }
}