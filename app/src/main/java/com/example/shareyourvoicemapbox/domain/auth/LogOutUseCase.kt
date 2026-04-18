package com.example.shareyourvoicemapbox.domain.auth

import com.example.shareyourvoicemapbox.data.repo.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() {
        authRepository.logOut()
    }
}