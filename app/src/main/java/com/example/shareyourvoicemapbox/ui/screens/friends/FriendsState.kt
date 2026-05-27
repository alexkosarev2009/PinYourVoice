package com.example.shareyourvoicemapbox.ui.screens.friends

import com.example.shareyourvoicemapbox.domain.entities.UserEntity

data class FriendsState(
    val userId: Long = -1L,
    val friends: List<UserEntity> = emptyList(),
    val error: String = ""
)