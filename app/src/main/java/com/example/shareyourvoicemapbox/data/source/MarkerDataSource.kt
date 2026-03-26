package com.example.shareyourvoicemapbox.data.source

import android.util.Log
import com.example.shareyourvoicemapbox.data.dto.MarkerDTO
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkerDataSource {
    val client = Network.client

    suspend fun getMarkers(): Result<List<MarkerDTO>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${Network.HOST}/api/marker")
            Log.d("KTOR", response.status.toString())
            Log.d("KTOR", response.bodyAsText())

            if (response.status != HttpStatusCode.OK) {
                error("Error: ${response.status}")
            }
            response.body<List<MarkerDTO>>()
        }
    }
}