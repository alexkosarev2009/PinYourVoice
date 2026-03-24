package com.example.shareyourvoicemapbox.domain.entities

data class MarkerEntity(
    val title: String,
    val lat: Double,
    val lng: Double,
    val imgUrl: String?,
    val audioUrl: String,
)