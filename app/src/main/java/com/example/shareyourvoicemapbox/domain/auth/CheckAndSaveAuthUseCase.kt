package com.example.shareyourvoicemapbox.domain.auth

import com.example.shareyourvoicemapbox.data.AuthRepository

class CheckAndSaveAuthUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        login: String,
        password: String,
    ): Result<Unit> {
        return authRepository.checkAndAuth(login, password).mapCatching { loginCompleted ->
            if (!loginCompleted) error("Login or password is incorrect!")
        }
    }
}