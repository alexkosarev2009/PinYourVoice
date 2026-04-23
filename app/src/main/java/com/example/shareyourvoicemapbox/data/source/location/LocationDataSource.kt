package com.example.shareyourvoicemapbox.data.source.location

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class LocationDataSource @Inject constructor(
    private val client: HttpClient
) {
    suspend fun reverseGeocode(lat: Double, lon: Double): String {
        val response: String = client.get("https://nominatim.openstreetmap.org/reverse") {
            parameter("format", "json")
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("zoom", 10)
            parameter("addressdetails", 1)

            headers {
                append(HttpHeaders.UserAgent, "MyApp/1.0")
                append(HttpHeaders.AcceptLanguage, "ru")
            }
        }.body()

        val json = Json.parseToJsonElement(response).jsonObject
        val address = json["address"]?.jsonObject

        val country = address?.get("country")?.jsonPrimitive?.content ?: ""
        val city =
            address?.get("city")?.jsonPrimitive?.content
                ?: address?.get("town")?.jsonPrimitive?.content
                ?: address?.get("village")?.jsonPrimitive?.content
                ?: ""
        Log.d("LOCATION", "$country, $city")
        return "$country, $city"
    }
}