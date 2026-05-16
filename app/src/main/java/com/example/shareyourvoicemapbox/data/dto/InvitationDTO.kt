package com.example.shareyourvoicemapbox.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitationDTO(
    @SerialName("id")
    val id: Long?,

    @SerialName("senderId")
    val senderId: Long?,

    @SerialName("senderName")
    val senderName: String?,

    @SerialName("senderUsername")
    val senderUsername: String?,

    @SerialName("senderAvatarUrl")
    val senderAvatarUrl: String?,

    @SerialName("createdAt")
    val createdAt: String?,
)