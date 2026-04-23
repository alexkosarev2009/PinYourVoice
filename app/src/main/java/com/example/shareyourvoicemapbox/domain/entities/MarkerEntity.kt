package com.example.shareyourvoicemapbox.domain.entities

data class MarkerEntity(
    val title: String,
    val location: String = "",
    val lat: Double,
    val lng: Double,
    val imageUrl: String?,
    val audioUrl: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarUrl: String,
)