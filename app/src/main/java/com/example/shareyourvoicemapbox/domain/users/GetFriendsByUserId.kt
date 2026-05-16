package com.example.shareyourvoicemapbox.domain.users

import com.example.shareyourvoicemapbox.data.repo.UserRepository
import com.example.shareyourvoicemapbox.domain.entities.UserEntity
import javax.inject.Inject

class GetFriendsByUserId @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long) : Result<List<UserEntity>> {
        return userRepository.getFriendsByUserId(userId)
    }
}