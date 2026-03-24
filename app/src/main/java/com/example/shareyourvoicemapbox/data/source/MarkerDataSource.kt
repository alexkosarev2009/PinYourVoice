package com.example.shareyourvoicemapbox.data.source

import com.example.shareyourvoicemapbox.data.dto.MarkerDTO
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkerDataSource {
    val client = Network.client

    suspend fun getMarkers(): Result<List<MarkerDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${Network.HOST}/api/marker")
            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body()
        }
    }
}