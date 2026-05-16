package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitationDTO(
    @SerialName("id")
    val id: Long?,

    @SerialName("senderId")
    val senderId: Long?,

    @SerialName("createdAt")
    val createdAt: String?,
)