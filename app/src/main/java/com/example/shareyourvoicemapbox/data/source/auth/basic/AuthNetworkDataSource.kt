package com.example.shareyourvoicemapbox.data.source.auth.basic

import com.example.shareyourvoicemapbox.data.constants.Constants
import com.example.shareyourvoicemapbox.data.dto.AuthResponseDTO
import com.example.shareyourvoicemapbox.data.dto.UserLoginDTO
import com.example.shareyourvoicemapbox.data.source.auth.bearer.TokenStorage
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
    private val tokenStorage: TokenStorage
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
            tokenStorage.save(result.body<AuthResponseDTO>().token)
            true
        }
    }
    fun logOut() {
        tokenStorage.clear()
    }
}