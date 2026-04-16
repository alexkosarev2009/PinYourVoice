package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresignResponseDTO(
    @SerialName("uploadUrl")
    val uploadUrl: String,

    @SerialName("key")
    val key: String
)