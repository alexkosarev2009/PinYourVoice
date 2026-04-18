package com.example.shareyourvoicemapbox.domain.auth

import com.example.shareyourvoicemapbox.data.dto.UserLoginDTO
import com.example.shareyourvoicemapbox.data.repo.AuthRepository
import javax.inject.Inject

class CheckAndSaveAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        dto: UserLoginDTO
    ): Result<Unit> {
        return authRepository.login(dto).mapCatching { loginCompleted ->
            if (!loginCompleted) error("Login or password is incorrect!")
        }
    }
}