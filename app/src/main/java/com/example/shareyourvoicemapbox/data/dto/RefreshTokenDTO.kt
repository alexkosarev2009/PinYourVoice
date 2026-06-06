package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenDTO(
    val refreshToken: String,
)