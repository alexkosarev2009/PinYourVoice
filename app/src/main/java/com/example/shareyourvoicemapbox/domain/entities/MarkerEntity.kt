package com.example.shareyourvoicemapbox.domain.entities

data class MarkerEntity(
    val id: Long = -1L,
    val title: String,
    val location: String = "",
    val lat: Double,
    val lng: Double,
    val imageUrl: String?,
    val audioUrl: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarUrl: String,
    val createdAt: String,
    val amplitudes: String,
    val icon: Int,
)