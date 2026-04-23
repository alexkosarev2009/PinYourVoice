package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateMarkerDTO(
    @SerialName("title")
    val title: String?,

    @SerialName("lat")
    val lat: Double?,

    @SerialName("lng")
    val lng: Double?,

    @SerialName("imageUrl")
    val imageUrl: String?,

    @SerialName("audioUrl")
    val audioUrl: String?,

)