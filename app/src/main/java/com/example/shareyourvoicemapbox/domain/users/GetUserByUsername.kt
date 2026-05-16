package com.example.shareyourvoicemapbox.domain.users

import com.example.shareyourvoicemapbox.data.repo.UserRepository
import com.example.shareyourvoicemapbox.domain.entities.UserEntity
import javax.inject.Inject

class GetUserByUsername @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        username: String
    ) : Result<UserEntity> {
        return userRepository.getUserByUsername(username)
    }
}