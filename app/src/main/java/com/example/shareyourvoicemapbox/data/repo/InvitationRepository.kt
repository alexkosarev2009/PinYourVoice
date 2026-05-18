package com.example.shareyourvoicemapbox.data.repo

import com.example.shareyourvoicemapbox.data.source.invitation.InvitationDataSource
import com.example.shareyourvoicemapbox.domain.entities.InvitationEntity
import javax.inject.Inject

class InvitationRepository @Inject constructor(
    private val invitationDataSource: InvitationDataSource
) {
    suspend fun getMyInvitations(): Result<List<InvitationEntity>> {
        return invitationDataSource.getMyInvitations().map { dTOS ->
            dTOS.mapNotNull { invitationDTO ->
                InvitationEntity(
                    id = invitationDTO.id ?: return@mapNotNull null,
                    senderId = invitationDTO.senderId ?: return@mapNotNull null,
                    createdAt = invitationDTO.createdAt ?: return@mapNotNull null,
                    senderAvatarUrl = invitationDTO.senderAvatarUrl ?: return@mapNotNull null,
                    senderName = invitationDTO.senderName ?: return@mapNotNull null,
                    senderUsername = invitationDTO.senderUsername ?: return@mapNotNull null,
                )
            }
        }
    }
    suspend fun invite(receiverId: Long): Result<Boolean> {
        return invitationDataSource.invite(receiverId)
    }
}