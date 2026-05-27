package com.example.shareyourvoicemapbox.ui.screens.friends

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.invitation.InviteFriendUseCase
import com.example.shareyourvoicemapbox.domain.users.GetFriendsByUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val getFriendsByUserId: GetFriendsByUserId,
    savedStateHandle: SavedStateHandle,
    private val inviteFriendUseCase: InviteFriendUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(FriendsState(
        userId = savedStateHandle.get<String>("userId")?.toLong() ?: -1L
    ))
    val state = _state.asStateFlow()

    fun getData() {
        viewModelScope.launch {
            getFriendsByUserId(_state.value.userId).fold(
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            error = error.message ?: ""
                        )
                    }

                },
                onSuccess = { friends ->
                    _state.update {
                        it.copy(friends = friends)
                    }
                }
            )
        }
    }
    fun invite(userId: Long) {
        viewModelScope.launch {
            inviteFriendUseCase(userId).fold(
                onSuccess = {

                },
                onFailure = { error ->
                    _state.update {
                        it.copy(error = error.message ?: "")
                    }
                }
            )
        }
    }
}