package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO (
    @SerialName("name")
    val name: String?,

    @SerialName("username")
    val username: String?,

    @SerialName("bio")
    val bio: String?,

    @SerialName("avatarUrl")
    val avatarUrl: String?,
)