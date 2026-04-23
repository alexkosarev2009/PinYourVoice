package com.example.shareyourvoicemapbox.data.repo

import com.example.shareyourvoicemapbox.data.dto.AuthResponseDTO
import com.example.shareyourvoicemapbox.data.dto.UserLoginDTO
import com.example.shareyourvoicemapbox.data.source.auth.basic.AuthNetworkDataSource
import com.example.shareyourvoicemapbox.data.source.auth.bearer.TokenStorage
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val networkDataSource: AuthNetworkDataSource,
) {
    suspend fun login(dto: UserLoginDTO): Result<Boolean> {
        return networkDataSource.login(dto)
    }

    fun logOut() {
        networkDataSource.logOut()
    }
}