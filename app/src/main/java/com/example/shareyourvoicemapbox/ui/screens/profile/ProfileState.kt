package com.example.shareyourvoicemapbox.ui.screens.profile

import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity
import com.example.shareyourvoicemapbox.domain.entities.UserEntity

data class ProfileState (
    val id: Long = -1L,
    val fullName: String = "",
    val userName: String = "",
    val friends: List<UserEntity> = emptyList(),
    val bio: String = "Empty bio",
    val error: String = "",
    val avatarUrl: String = "",
    val userId: Long = -1L,
    val markers: List<MarkerEntity> = emptyList(),
    val isRefreshing: Boolean = false,
)