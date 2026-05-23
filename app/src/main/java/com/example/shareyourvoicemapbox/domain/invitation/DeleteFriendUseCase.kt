package com.example.shareyourvoicemapbox.domain.invitation

import com.example.shareyourvoicemapbox.data.repo.InvitationRepository
import javax.inject.Inject

class DeleteFriendUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository
) {
    suspend operator fun invoke(receiverId: Long): Result<Unit> {
       return invitationRepository.delete(receiverId)
    }
}