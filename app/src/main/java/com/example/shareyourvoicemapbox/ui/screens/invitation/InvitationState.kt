package com.example.shareyourvoicemapbox.ui.screens.invitation

import com.example.shareyourvoicemapbox.domain.entities.InvitationEntity

data class InvitationState(
    val invitations: List<InvitationEntity> = emptyList(),
    val error: String = ""
)