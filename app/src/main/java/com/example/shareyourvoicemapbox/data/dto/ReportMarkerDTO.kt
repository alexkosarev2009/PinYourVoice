package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportMarkerDTO(
    @SerialName("markerId")
    val markerId: Long?,
    @SerialName("reason")
    val reason: String?,
)