package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarkerDTO(
    @SerialName("title")
    val title: String?,

    @SerialName("lat")
    val lat: Double?,

    @SerialName("lng")
    val lng: Double?,

    @SerialName("img_url")
    val imgUrl: String?,

    @SerialName("audio_url")
    val audioUrl: String?,
)