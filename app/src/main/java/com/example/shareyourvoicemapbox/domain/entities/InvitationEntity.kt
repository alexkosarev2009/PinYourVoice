package com.example.shareyourvoicemapbox.domain.entities

data class InvitationEntity(
    val id: Long = -1L,
    val senderId: Long,
    val createdAt: String,
)