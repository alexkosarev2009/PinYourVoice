package com.example.shareyourvoicemapbox.data.source.presign

import android.net.Uri
import com.example.shareyourvoicemapbox.data.dto.PresignResponseDTO
import com.example.shareyourvoicemapbox.data.source.Network
import com.example.shareyourvoicemapbox.data.source.auth.bearer.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.cio.readChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.io.File

class PresignDataSource @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun getPresign(
        fileName: String,
        contentType: String,
    ): Result<PresignResponseDTO> = withContext(Dispatchers.IO) {
        runCatching {
            val response = client.get("${Network.HOST}/api/s3/presign") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
                parameter("fileName", fileName)
                parameter("contentType", contentType)
            }
            if (response.status != HttpStatusCode.OK) {
                error("Get upload url failed: ${response.status}")
            }
            response.body()
        }
    }
    suspend fun uploadFile(
        fileName: String,
        contentType: String,
        filePath: String,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(filePath)
            val data = getPresign(fileName, contentType).getOrThrow()

            val response = client.put(data.uploadUrl) {
                header("Content-Type", contentType)
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
                setBody(file.readChannel())
            }
            if (!response.status.isSuccess()) {
                error("Upload failed: ${response.status}")
            }
            data.key
        }
    }
}