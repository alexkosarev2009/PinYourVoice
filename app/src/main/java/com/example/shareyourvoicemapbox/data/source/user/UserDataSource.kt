package com.example.shareyourvoicemapbox.data.source.user

import android.util.Log
import com.example.shareyourvoicemapbox.data.constants.Constants.HOST
import com.example.shareyourvoicemapbox.data.dto.UserDTO
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
            val result = client.get("${HOST}/api/users") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }
    suspend fun getUserById(id: Long): Result<UserDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.get("${HOST}/api/users/{$id}") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }
    suspend fun getUserByUsername(username: String): Result<UserDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.get("${HOST}/api/users/by-username") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
                parameter("username", username)
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }
    suspend fun getMe(): Result<UserDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.get("${HOST}/api/users/me") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
            val entity = result.body<UserDTO>()
            entity
        }
    }
}