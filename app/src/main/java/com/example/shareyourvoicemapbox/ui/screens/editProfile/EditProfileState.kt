package com.example.shareyourvoicemapbox.ui.screens.editProfile

import android.net.Uri

data class EditProfileState(
    val avatarUrl: String = "",
    val avatarUri: Uri? = null,
    val name: String = "",
    val username: String = "",
    val bio: String = "",
    val nameInput: String = "",
    val bioInput: String = "",
)