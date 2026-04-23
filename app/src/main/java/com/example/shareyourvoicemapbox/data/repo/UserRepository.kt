package com.example.shareyourvoicemapbox.data.repo

import com.example.shareyourvoicemapbox.data.exceptions.AppException
import com.example.shareyourvoicemapbox.data.source.user.UserDataSource
import com.example.shareyourvoicemapbox.domain.entities.UserEntity
import javax.inject.Inject

class UserRepository @Inject constructor(
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
    suspend fun getUserById(id: Long): Result<UserEntity> {
        return userInfoDataSource.getUserById(id).map { userDTO ->
            UserEntity(
                name = userDTO.name ?: throw AppException.UserNotFoundException(),
                username = userDTO.username ?: throw AppException.UserNotFoundException(),
                bio = userDTO.bio ?: "Empty bio",
                avatarUrl = userDTO.avatarUrl ?: "default/avatar.jpg",
            )
        }
    }
    suspend fun getUserByUsername(username: String): Result<UserEntity?> {
        return userInfoDataSource.getUserByUsername(username).map { userDTO ->
            UserEntity(
                name = userDTO.name ?: return@map null,
                username = userDTO.username ?: return@map null,
                bio = userDTO.bio ?: "Empty bio",
                avatarUrl = userDTO.avatarUrl ?: "default/avatar.jpg",
            )
        }
    }
    suspend fun getMe(): Result<UserEntity> {
        return userInfoDataSource.getMe().map { userDTO ->
            UserEntity(
                name = userDTO.name ?: "name",
                username = userDTO.username ?: "username",
                bio = userDTO.bio ?: "Empty bio",
                avatarUrl = userDTO.avatarUrl ?: "default/avatar.jpg",
            )
        }
    }

}