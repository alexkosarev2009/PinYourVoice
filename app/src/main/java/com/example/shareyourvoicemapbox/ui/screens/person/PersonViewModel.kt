package com.example.shareyourvoicemapbox.ui.screens.person

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.entities.UserEntity
import com.example.shareyourvoicemapbox.domain.invitation.DeleteFriendUseCase
import com.example.shareyourvoicemapbox.domain.invitation.InviteFriendUseCase
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersByAuthorIdUseCase
import com.example.shareyourvoicemapbox.domain.users.GetFriendsByUserId
import com.example.shareyourvoicemapbox.domain.users.GetUserByUsername
import com.example.shareyourvoicemapbox.ui.screens.profile.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PersonViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getUserByUsername: GetUserByUsername,
    private val getMarkersByAuthorIdUseCase: GetMarkersByAuthorIdUseCase,
    private val getFriendsByUserId: GetFriendsByUserId,
    private val inviteFriendUseCase: InviteFriendUseCase,
    private val deleteFriendUseCase: DeleteFriendUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _currentUser: MutableStateFlow<UserEntity?> = MutableStateFlow(null)
    val currentUser = _currentUser.asStateFlow()

    private val _friendAdded = MutableStateFlow(false)
    val friendAdded = _friendAdded.asStateFlow()

    private val _isDeleteFriendDialogOpened = MutableStateFlow(false)
    val isDeleteFriendDialogOpened = _isDeleteFriendDialogOpened.asStateFlow()

    fun getPersonInfo() {
        _state.update {
            it.copy(isRefreshing = true)
        }
        val username = savedStateHandle.get<String>("username")
        Log.d("PERSON", username.toString())
        if (!username.isNullOrEmpty()) {
            viewModelScope.launch {
                getUserByUsername(username).fold(
                    onSuccess = { user ->
                        _currentUser.emit(user)
                        _state.emit(
                            ProfileState(
                                fullName = user.name,
                                userName = user.username,
                                bio = user.bio ?: "Empty bio",
                                error = "",
                                avatarUrl = user.avatarUrl ?: "",
                                userId = user.id,
                                markers = emptyList(),
                                isRefreshing = false
                            )
                        )
                        getFriendsByUserId(user.id).fold(
                            onSuccess = { friends ->
                                _state.update {
                                    it.copy(friends = friends)
                                }
                            },
                            onFailure = { error ->
                                _state.update {
                                    it.copy(error = error.message ?: "")
                                }
                            }
                        )
                        getMarkersByAuthorIdUseCase(user.id).fold(
                            onFailure = { error ->
                                _state.update {
                                    it.copy(error = error.message ?: "")
                                }
                            },
                            onSuccess = { markers ->
                                _state.update {
                                    it.copy(markers = markers)
                                }
                            }
                        )
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(error = error.message ?: "")
                        }
                    }
                )
            }
        }
        _state.update {
            it.copy(isRefreshing = false)
        }
    }

    fun onMenuClick() {

    }

    fun clearUser() {
        viewModelScope.launch {
            _currentUser.emit(null)
        }
        savedStateHandle.remove<String>("username")
    }

    fun invite(receiverId: Long) {
        viewModelScope.launch {
            inviteFriendUseCase(receiverId)
            _friendAdded.emit(true)
        }
    }

    fun deleteFriend(id: Long) {
        viewModelScope.launch {
            deleteFriendUseCase(id).fold(
                onSuccess = {
                    _friendAdded.emit(false)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(error = error.message ?: "")
                    }
                }
            )
        }
    }

    fun openDeleteFriendDialog() {
        viewModelScope.launch {
            _isDeleteFriendDialogOpened.emit(true)
        }
    }
    fun closeDeleteFriendDialog() {
        viewModelScope.launch {
            _isDeleteFriendDialogOpened.emit(false)
        }
    }
}