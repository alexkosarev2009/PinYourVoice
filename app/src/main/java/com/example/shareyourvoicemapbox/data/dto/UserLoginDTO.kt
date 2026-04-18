package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLoginDTO(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String
)