package com.example.shareyourvoicemapbox.domain.users

import com.example.shareyourvoicemapbox.data.dto.UpdateUserDTO
import com.example.shareyourvoicemapbox.data.dto.UserDTO
import com.example.shareyourvoicemapbox.data.repo.UserRepository
import javax.inject.Inject

class UpdateMeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userDTO: UpdateUserDTO): Result<Boolean> {
        return userRepository.updateMe(userDTO)
    }
}