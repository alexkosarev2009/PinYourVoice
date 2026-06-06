package com.example.shareyourvoicemapbox.data.source.auth.network

import com.example.shareyourvoicemapbox.data.constants.Constants
import com.example.shareyourvoicemapbox.data.dto.AuthResponseDTO
import com.example.shareyourvoicemapbox.data.dto.RefreshTokenDTO
import com.example.shareyourvoicemapbox.data.dto.UserLoginDTO
import com.example.shareyourvoicemapbox.data.dto.UserRegisterDTO
import com.example.shareyourvoicemapbox.data.source.auth.storage.TokenStorage
import com.example.shareyourvoicemapbox.domain.auth.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthNetworkDataSource @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
) {
    suspend fun login(dto: UserLoginDTO): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.post("${Constants.HOST}/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(dto)
            }
            if (result.status != HttpStatusCode.OK) {
                tokenStorage.clear()
                error("Auth error: ${result.status}")
            }
            val body = result.body<AuthResponseDTO>()
            tokenStorage.save(body.accessToken, body.refreshToken)
            sessionManager.logIn()
            true
        }
    }
    suspend fun register(dto: UserRegisterDTO): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.post("${Constants.HOST}/api/users") {
                contentType(ContentType.Application.Json)
                setBody(dto)
            }
            if (result.status != HttpStatusCode.Created) {
                error("Register error: ${result.status}")
            }
            true
        }
    }
    fun logOut() {
        tokenStorage.clear()
        sessionManager.logout()
    }
}