package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MarkerDTO(
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

    @SerialName("authorName")
    val authorName: String?,

    @SerialName("authorUsername")
    val authorUsername: String?,

    @SerialName("authorAvatarUrl")
    val authorAvatarUrl: String?,

    @SerialName("createdAt")
    val createdAt: String?,
)