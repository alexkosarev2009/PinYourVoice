package com.example.shareyourvoicemapbox.data.source.marker

import android.util.Log
import com.example.shareyourvoicemapbox.data.constants.Constants.HOST
import com.example.shareyourvoicemapbox.data.dto.CreateMarkerDTO
import com.example.shareyourvoicemapbox.data.dto.MarkerDTO
import com.example.shareyourvoicemapbox.data.dto.RefreshTokenDTO
import com.example.shareyourvoicemapbox.data.source.auth.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarkerDataSource @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) {

    suspend fun getMarkers(): Result<List<MarkerDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${HOST}/api/markers") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<List<MarkerDTO>>()
        }
    }
    suspend fun getAvailableMarkers(): Result<List<MarkerDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${HOST}/api/markers/available") {
                Log.d("TOKEN", tokenStorage.getAccessToken() ?: "null")
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<List<MarkerDTO>>()
        }
    }
    suspend fun getPublicMarkers(): Result<List<MarkerDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${HOST}/api/markers/public") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<List<MarkerDTO>>()
        }
    }
    suspend fun getMarkersByAuthorId(authorId: Long): Result<List<MarkerDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.get("${HOST}/api/markers/by-author-id") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
                parameter("authorId", authorId)
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }

    suspend fun postMarker(dto: CreateMarkerDTO): Result<MarkerDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post("${HOST}/api/markers") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
                contentType(ContentType.Application.Json)
                setBody(dto)
            }
            if (response.status != HttpStatusCode.Created) {
                error("Error: ${response.status}")
            }
            response.body<MarkerDTO>()
        }
    }

    suspend fun deleteMarker(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.delete("$HOST/api/markers/$id") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
            }
            if (result.status != HttpStatusCode.NoContent) {
                error("Error: ${result.status}")
            }
        }
    }

    suspend fun getMarkerById(id: Long): Result<MarkerDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${HOST}/api/markers/$id") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
                contentType(ContentType.Application.Json)
            }
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<MarkerDTO>()
        }
    }

    suspend fun searchMarkerByTitle(
        query: String
    ): Result<List<MarkerDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${HOST}/api/markers/search") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.getAccessToken()}")
                parameter("query", query)
            }
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<List<MarkerDTO>>()
        }
    }
}