package com.example.shareyourvoicemapbox.ui.screens.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.invitation.AcceptInvitationUseCase
import com.example.shareyourvoicemapbox.domain.invitation.DeclineInvitationUseCase
import com.example.shareyourvoicemapbox.domain.invitation.GetMyInvitationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val getMyInvitationsUseCase: GetMyInvitationsUseCase,
    private val acceptInvitationUseCase: AcceptInvitationUseCase,
    private val declineInvitationUseCase: DeclineInvitationUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(InvitationState())
    val state = _state.asStateFlow()

    fun getInvitations() {
        viewModelScope.launch {
            getMyInvitationsUseCase().fold(
                onSuccess = { invitations ->
                    _state.update {
                        it.copy(invitations = invitations)
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(error = error.message ?: "")
                    }
                }
            )
        }
    }

    fun declineInvitation(id: Long) {
        viewModelScope.launch {
            declineInvitationUseCase(id).fold(
                onFailure = {

                },
                onSuccess = {

                }
            )
        }
        _state.update {
            it.copy(invitations = _state.value.invitations.filter { inv ->
                inv.id != id
            })
        }
    }
    fun acceptInvitation(id: Long) {
        viewModelScope.launch {
            acceptInvitationUseCase(id).fold(
                onFailure = {

                },
                onSuccess = {

                }
            )
        }
        _state.update {
            it.copy(invitations = _state.value.invitations.filter { inv ->
                inv.id != id
            })
        }
    }

}