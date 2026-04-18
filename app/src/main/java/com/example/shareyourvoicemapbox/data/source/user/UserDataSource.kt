package com.example.shareyourvoicemapbox.data.source.user

import com.example.shareyourvoicemapbox.data.dto.UserDTO
import com.example.shareyourvoicemapbox.data.source.Network
import com.example.shareyourvoicemapbox.data.source.auth.bearer.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun getUsers(): Result<List<UserDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/users") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
            }
            if (result.status != HttpStatusCode.Companion.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }
    suspend fun getUserById(id: Long): Result<UserDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/users/{$id}") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
            }
            if (result.status != HttpStatusCode.Companion.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }
    suspend fun getUserByUsername(username: String): Result<UserDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/users/by-username") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
                parameter("username", username)
            }
            if (result.status != HttpStatusCode.Companion.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }
}