package com.example.shareyourvoicemapbox.ui.screens.editProfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.UploadFileUseCase
import com.example.shareyourvoicemapbox.domain.users.GetMeUseCase
import com.example.shareyourvoicemapbox.ui.screens.edit.MAX_TITLE_LEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getMeUseCase: GetMeUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
) : ViewModel() {
    fun pickNewAvatar(uri: Uri) {
        _state.update {
            it.copy(avatarUri = uri)
        }
    }

    fun changeBio(newBio: String) {
        if (newBio.length <= MAX_BIO_LEN) {
            _state.update { state ->
                state.copy(bioInput = newBio)
            }
        }
    }

    fun onNameChange(newName: String) {
        if (newName.length <= MAX_NAME_LEN) {
            _state.update { state ->
                state.copy(nameInput = newName)
            }
        }
    }

    init {
        viewModelScope.launch {
            getMeUseCase().fold(
                onSuccess = { me ->
                    _state.update {
                        it.copy(
                            avatarUrl = me.avatarUrl ?: "",
                            name = me.name,
                            username = me.username,
                            bio = me.bio ?: "Empty bio",
                            bioInput = me.bio ?: "Empty bio",
                            nameInput = me.name
                        )
                    }
                },
                onFailure = {

                },
            )
        }
    }

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()


}