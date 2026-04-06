package com.example.shareyourvoicemapbox.data

import com.example.shareyourvoicemapbox.data.source.AuthLocalDataSource
import com.example.shareyourvoicemapbox.data.source.AuthNetworkDataSource

class AuthRepository(
    private val networkDataSource: AuthNetworkDataSource,
    private val localDataSource: AuthLocalDataSource
) {
    suspend fun checkAndAuth(
        login: String,
        password: String,
    ): Result<Boolean> {
        localDataSource.setToken(login, password)
        return networkDataSource.checkAuth(
            localDataSource.token ?: error("No token")
        ).onFailure {
            localDataSource.clearToken()
        }.onSuccess { loginCompleted ->
            if (!loginCompleted) localDataSource.clearToken()
        }
    }
}