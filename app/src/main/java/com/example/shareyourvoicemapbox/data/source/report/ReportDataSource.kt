package com.example.shareyourvoicemapbox.data.source.report

import com.example.shareyourvoicemapbox.data.constants.Constants
import com.example.shareyourvoicemapbox.data.dto.AuthResponseDTO
import com.example.shareyourvoicemapbox.data.dto.ReportMarkerDTO
import com.example.shareyourvoicemapbox.data.dto.UserLoginDTO
import com.example.shareyourvoicemapbox.data.dto.UserRegisterDTO
import com.example.shareyourvoicemapbox.data.source.auth.bearer.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportDataSource @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun reportMarker(dto: ReportMarkerDTO): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.post("${Constants.HOST}/api/report/marker") {
                header(HttpHeaders.Authorization, "Bearer ${tokenStorage.get()}")
                contentType(ContentType.Application.Json)
                setBody(dto)
            }
            if (result.status != HttpStatusCode.OK) {
                error("Report error: ${result.status}")
            }
            true
        }
    }

}