package com.example.shareyourvoicemapbox.domain.auth

class CheckAuthFormatUseCase {
    operator fun invoke(
        login: String,
        password: String
    ): Boolean {
        return login.isNotEmpty() && password.isNotEmpty()
    }
}