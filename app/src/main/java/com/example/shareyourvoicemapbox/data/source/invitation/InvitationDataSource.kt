package com.example.shareyourvoicemapbox.data.source.invitation

import com.example.shareyourvoicemapbox.data.constants.Constants.HOST
import com.example.shareyourvoicemapbox.data.dto.InvitationDTO
import com.example.shareyourvoicemapbox.data.source.auth.bearer.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InvitationDataSource @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun getMyInvitations(): Result<List<InvitationDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${HOST}/api/friends/invitations") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
            }
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<List<InvitationDTO>>()
        }
    }
}