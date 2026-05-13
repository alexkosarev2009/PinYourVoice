package com.example.shareyourvoicemapbox.domain.auth

import com.example.shareyourvoicemapbox.data.dto.UserRegisterDTO
import com.example.shareyourvoicemapbox.data.repo.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        dto: UserRegisterDTO
    ): Result<Unit> {
        return authRepository.register(dto).mapCatching { isOkay ->
            if (!isOkay) error("An error occurred during register")
        }
    }
}