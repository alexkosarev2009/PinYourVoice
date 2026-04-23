package com.example.shareyourvoicemapbox.data.source.marker

import com.example.shareyourvoicemapbox.data.constants.Constants.HOST
import com.example.shareyourvoicemapbox.data.dto.CreateMarkerDTO
import com.example.shareyourvoicemapbox.data.dto.MarkerDTO
import com.example.shareyourvoicemapbox.data.source.auth.bearer.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
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
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
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
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
                contentType(ContentType.Application.Json)
                setBody(dto)
            }
            if (response.status != HttpStatusCode.Created) {
                error("Error: ${response.status}")
            }
            response.body<MarkerDTO>()
        }
    }
}