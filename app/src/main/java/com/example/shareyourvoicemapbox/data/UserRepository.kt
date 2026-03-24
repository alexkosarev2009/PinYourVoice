package com.example.shareyourvoicemapbox.data

import com.example.shareyourvoicemapbox.data.source.UserDataSource
import com.example.shareyourvoicemapbox.domain.entities.UserEntity

class UserRepository(
    val userInfoDataSource: UserDataSource
) {
    suspend fun getUsers(): Result<List<UserEntity>> {
        return userInfoDataSource.getUsers().map { dTOS ->
            dTOS.mapNotNull { userDTO ->
                UserEntity(
                    name = userDTO.name ?: return@mapNotNull null,
                    username = userDTO.username ?: return@mapNotNull null,
                    bio = userDTO.bio ?: "Empty bio",
                    avatarUrl = userDTO.avatarUrl ?: "default/avatar.jpg",
                )
            }
        }
    }
}