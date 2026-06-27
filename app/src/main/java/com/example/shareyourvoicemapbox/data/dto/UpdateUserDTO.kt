package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDTO (
    @SerialName("name")
    val name: String?,
    @SerialName("username")
    val username: String?,
    @SerialName("avatarUrl")
    val avatarUrl: String?,
    @SerialName("bio")
    val bio: String?,
)
