package com.example.shareyourvoicemapbox.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.auth.LogOutUseCase
import com.example.shareyourvoicemapbox.domain.location.ReverseGeocodeUseCase
import com.example.shareyourvoicemapbox.domain.markers.DeleteMarkerUseCase
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersByAuthorIdUseCase
import com.example.shareyourvoicemapbox.domain.users.GetMeUseCase
import com.example.shareyourvoicemapbox.domain.users.GetMyFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val getMarkersByAuthorIdUseCase: GetMarkersByAuthorIdUseCase,
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase,
    private val getMyFriendsUseCase: GetMyFriendsUseCase,
    private val deleteMarkerUseCase: DeleteMarkerUseCase,
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val _location = MutableStateFlow("")
    val location = _location.asStateFlow()

    private val _showDeleteMarkerDialog = MutableStateFlow(false)
    val showDeleteMarkerDialog = _showDeleteMarkerDialog.asStateFlow()

    private val _currentMarkerId = MutableStateFlow(-1L)
    val currentMarkerId = _currentMarkerId.asStateFlow()

    fun onLogOutClick() {
        logOutUseCase()
    }


    init {
        loadUserInfo()
    }

    fun reverseGeocode() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    markers = state.markers.map { marker ->
                        marker.copy(
                            location = reverseGeocodeUseCase(marker.lat, marker.lng)
                        )
                    }
                )
            }
        }
    }

    fun getMarkers(userId: Long) {
        viewModelScope.launch {
            getMarkersByAuthorIdUseCase(userId).fold(
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "")
                    }
                },
                onSuccess = { markers ->
                    _uiState.update {
                        it.copy(markers = markers)
                    }
                }
            )
        }
    }

    fun loadUserInfo() {
        _uiState.update {
            it.copy(
                isRefreshing = true
            )
        }
        viewModelScope.launch {
            getMeUseCase().fold(
                onSuccess = { user ->
                    getMarkers(user.id)
                    getMyFriendsUseCase().fold(
                        onSuccess = { friends ->
                            _uiState.update {
                                it.copy(friends = friends)
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(error = error.message ?: "")
                            }
                        }
                    )
                    _uiState.update {
                        it.copy(
                            id = user.id,
                            fullName = user.name,
                            userName = user.username,
                            bio = user.bio ?: "Empty bio",
                            avatarUrl = user.avatarUrl ?: "",
                            userId = user.id
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "")
                    }
                }
            )
            _uiState.update {
                it.copy(
                    isRefreshing = false
                )
            }
        }
    }
    fun onMenuClick() {
        logOutUseCase()
    }

    fun deleteMarker(id: Long) {
        viewModelScope.launch {
            deleteMarkerUseCase(id).fold(
                onSuccess = {
                    getMarkers(_uiState.value.id)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "")
                    }
                }
            )
        }
    }
    fun openDeleteMarkerDialog(id: Long) {
        viewModelScope.launch {
            _currentMarkerId.emit(id)
            _showDeleteMarkerDialog.emit(true)
        }
    }
    fun closeDeleteMarkerDialog() {
        viewModelScope.launch {
            _showDeleteMarkerDialog.emit(false)
        }
    }

    fun closeLogOutDialog() {
        _uiState.update {
            it.copy(showLogOutDialog = false)
        }
    }
    fun openLogOutDialog() {
        _uiState.update {
            it.copy(showLogOutDialog = true)
        }
    }
}