package com.example.shareyourvoicemapbox.domain.entities

data class InvitationEntity(
    val id: Long = -1L,
    val senderId: Long,
    val senderName: String,
    val senderUsername: String,
    val senderAvatarUrl: String,
    val createdAt: String,
)