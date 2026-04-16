package com.example.shareyourvoicemapbox.data.source.auth

import com.example.shareyourvoicemapbox.data.source.Network
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthNetworkDataSource {
    val client = Network.client

    suspend fun checkAuth(token: String): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.get("${Network.HOST}/api/users/login") {
                header(HttpHeaders.Authorization, token)
            }
            result.status == HttpStatusCode.OK
        }
    }
}