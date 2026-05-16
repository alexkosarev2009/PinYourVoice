package com.example.shareyourvoicemapbox.domain.invitation

import com.example.shareyourvoicemapbox.data.repo.InvitationRepository
import com.example.shareyourvoicemapbox.domain.entities.InvitationEntity
import javax.inject.Inject

class GetMyInvitationsUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository
) {
    suspend operator fun invoke() : Result<List<InvitationEntity>> {
        return invitationRepository.getMyInvitations()
    }
}