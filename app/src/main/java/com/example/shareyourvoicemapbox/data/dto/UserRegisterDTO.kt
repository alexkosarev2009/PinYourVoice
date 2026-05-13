package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterDTO(
    @SerialName("name")
    val name: String,
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
)