package com.example.shareyourvoicemapbox.ui.screens.editProfile

import android.net.Uri
import com.example.shareyourvoicemapbox.data.dto.UserDTO
import com.example.shareyourvoicemapbox.domain.entities.UserEntity

data class EditProfileState(
    val user: UserEntity? = null,
    val isLoading: Boolean = false,
    val initialAvatarUrl: String = "",
    val error: String = "",
    val avatarUrl: String = "",
    val avatarUri: Uri? = null,
    val name: String = "",
    val username: String = "",
    val bio: String = "",
    val nameInput: String = "",
    val bioInput: String = "",
    val imagePath: String = "",
    val showCropScreen: Boolean = false,
)