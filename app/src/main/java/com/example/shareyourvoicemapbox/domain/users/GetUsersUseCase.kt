package com.example.shareyourvoicemapbox.domain.users

import com.example.shareyourvoicemapbox.data.repo.UserRepository
import com.example.shareyourvoicemapbox.domain.entities.UserEntity

class GetUsersUseCase(
    val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<UserEntity>> {
        return userRepository.getUsers()
    }
}