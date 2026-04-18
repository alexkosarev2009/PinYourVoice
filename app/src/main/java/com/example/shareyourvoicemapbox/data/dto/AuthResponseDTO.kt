package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDTO(
    val token: String
)