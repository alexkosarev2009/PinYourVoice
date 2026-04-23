package com.example.shareyourvoicemapbox.ui.screens.profile

data class ProfileState (
    val fullName: String = "defaultName",
    val userName: String = "defaultUserName",
    val bio: String = "Empty bio",
    val error: String = "",
    val avatarUrl: String = ""
)