package com.example.shareyourvoicemapbox.ui.screens.profile

import com.example.shareyourvoicemapbox.domain.entities.MarkerEntity

data class ProfileState (
    val fullName: String = "",
    val userName: String = "",
    val bio: String = "Empty bio",
    val error: String = "",
    val avatarUrl: String = "",
    val userId: Long = 0,
    val markers: List<MarkerEntity> = emptyList(),
    val isRefreshing: Boolean = false,
)