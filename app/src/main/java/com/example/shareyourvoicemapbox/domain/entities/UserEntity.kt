package com.example.shareyourvoicemapbox.domain.entities

data class UserEntity(
    val name: String,
    val username: String,
    val bio: String?,
    val avatarUrl: String?,
)