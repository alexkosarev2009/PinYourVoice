package com.example.shareyourvoicemapbox.domain.invitation

import com.example.shareyourvoicemapbox.data.source.invitation.InvitationDataSource
import javax.inject.Inject

class AcceptInvitationUseCase @Inject constructor(
    private val invitationDataSource: InvitationDataSource
) {
    suspend operator fun invoke(id: Long): Result<Boolean> {
        return invitationDataSource.acceptInvitation(id)
    }
}