package com.example.shareyourvoicemapbox.data.source.invitation

import com.example.shareyourvoicemapbox.data.constants.Constants.HOST
import com.example.shareyourvoicemapbox.data.dto.InvitationDTO
import com.example.shareyourvoicemapbox.data.source.auth.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
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
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<List<InvitationDTO>>()
        }
    }
    suspend fun declineInvitation(id: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.delete("${HOST}/api/friends/invitations/$id/decline") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (response.status != HttpStatusCode.NoContent) {
                error("Error: ${response.status}")
            }
            true
        }
    }
    suspend fun acceptInvitation(id: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.patch("${HOST}/api/friends/invitations/$id/accept") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            true
        }
    }
    suspend fun invite(receiverId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post("${HOST}/api/friends/invite/$receiverId") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (response.status != HttpStatusCode.Created) {
                error("Error: ${response.status}")
            }
            true
        }
    }
    suspend fun deleteFriend(receiverId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.delete("${HOST}/api/friends/delete/$receiverId") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (response.status != HttpStatusCode.NoContent) {
                error("Error: ${response.status}")
            }
        }
    }
}