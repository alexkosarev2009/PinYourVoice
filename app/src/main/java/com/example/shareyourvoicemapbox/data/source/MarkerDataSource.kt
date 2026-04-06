package com.example.shareyourvoicemapbox.data.source

import com.example.shareyourvoicemapbox.data.dto.MarkerDTO
import com.example.shareyourvoicemapbox.data.dto.CreateMarkerDTO
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkerDataSource {
    val client = Network.client

    suspend fun getMarkers(): Result<List<MarkerDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${Network.HOST}/api/markers")
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<List<MarkerDTO>>()
        }
    }
    suspend fun postMarker(dto: CreateMarkerDTO): Result<MarkerDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.post("${Network.HOST}/api/markers") {
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