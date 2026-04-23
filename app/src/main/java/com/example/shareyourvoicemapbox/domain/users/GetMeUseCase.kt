package com.example.shareyourvoicemapbox.domain.users

import com.example.shareyourvoicemapbox.data.repo.UserRepository
import com.example.shareyourvoicemapbox.domain.entities.UserEntity
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<UserEntity> {
        return userRepository.getMe()
    }
}