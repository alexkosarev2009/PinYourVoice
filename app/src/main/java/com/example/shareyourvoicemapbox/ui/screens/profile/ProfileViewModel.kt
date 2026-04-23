package com.example.shareyourvoicemapbox.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.auth.LogOutUseCase
import com.example.shareyourvoicemapbox.domain.entities.UserEntity
import com.example.shareyourvoicemapbox.domain.location.ReverseGeocodeUseCase
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersByAuthorIdUseCase
import com.example.shareyourvoicemapbox.domain.users.GetMeUseCase
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
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val _location = MutableStateFlow("")
    val location = _location.asStateFlow()

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

    fun loadUserInfo() {
        viewModelScope.launch {
            getMeUseCase().fold(
                onSuccess = { user ->
                    getMarkersByAuthorIdUseCase(user.id).fold(
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
                    _uiState.update {
                        it.copy(
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
        }
    }
    fun onMenuClick() {
        logOutUseCase()
    }
}