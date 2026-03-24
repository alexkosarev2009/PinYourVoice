package com.example.shareyourvoicemapbox.domain

import com.example.shareyourvoicemapbox.data.UserRepository
import com.example.shareyourvoicemapbox.domain.entities.UserEntity

class GetUsersUseCase(
    val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<UserEntity>> {
        return userRepository.getUsers()
    }
}