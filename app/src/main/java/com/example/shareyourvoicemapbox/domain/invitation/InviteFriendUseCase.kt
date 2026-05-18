package com.example.shareyourvoicemapbox.domain.invitation

import com.example.shareyourvoicemapbox.data.repo.InvitationRepository
import javax.inject.Inject

class InviteFriendUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository
) {
    suspend operator fun invoke(receiverId: Long): Result<Boolean> {
        return invitationRepository.invite(receiverId)
    }
}