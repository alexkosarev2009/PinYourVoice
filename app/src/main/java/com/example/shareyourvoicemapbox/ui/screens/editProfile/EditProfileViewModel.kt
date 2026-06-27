package com.example.shareyourvoicemapbox.ui.screens.editProfile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.data.constants.Constants
import com.example.shareyourvoicemapbox.data.dto.UpdateUserDTO
import com.example.shareyourvoicemapbox.domain.UploadFileUseCase
import com.example.shareyourvoicemapbox.domain.users.GetMeUseCase
import com.example.shareyourvoicemapbox.domain.users.UpdateMeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getMeUseCase: GetMeUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val updateMeUseCase: UpdateMeUseCase
) : ViewModel() {
    fun pickNewAvatar(uri: Uri) {
        _state.update {
            it.copy(avatarUri = uri)
        }
    }

    fun getFileFromUri(context: Context, uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open input stream")

        val file = File.createTempFile("upload_", ".tmp", context.cacheDir)

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        _state.update {
            it.copy(imagePath = file.absolutePath)
        }
    }

    fun getFileFromBitmap(
        bitmap: Bitmap,
        context: Context
    ) {
        val file = File(
            context.cacheDir,
            "cropped_avatar_${System.currentTimeMillis()}.png"
        )

        FileOutputStream(file).use { out ->
            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            )
            out.flush()
        }

        _state.update {
            it.copy(imagePath = file.absolutePath,
                avatarUrl = file.absolutePath)
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
                            nameInput = me.name,
                            user = me,
                            initialAvatarUrl = me.avatarUrl ?: "",
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


    fun showCropScreen() {
        _state.update {
            it.copy(showCropScreen = true)
        }
    }
    fun hideCropScreen() {
        _state.update {
            it.copy(showCropScreen = false)
        }
    }

    fun updateMe() {
        _state.update {
            it.copy(isLoading = true)
        }
        if (_state.value.initialAvatarUrl == _state.value.avatarUrl) {
            viewModelScope.launch {
                updateMeUseCase(
                    UpdateUserDTO(
                        name = state.value.nameInput,
                        username = state.value.user?.username,
                        bio = state.value.bioInput,
                        avatarUrl = state.value.avatarUrl
                    )
                )
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
        else {
            viewModelScope.launch {
                val imageResult = uploadFileUseCase(
                    fileName = "image.jpg",
                    contentType = "image/jpeg",
                    filePath = _state.value.imagePath
                ).onFailure {
                    _state.update {
                        it.copy(isLoading = false,
                            error = "Failed to upload image")
                    }
                    return@launch
                }
                Log.d("UPLOAD", "isSuccess=${imageResult.isSuccess}")
                Log.d("UPLOAD", "isFailure=${imageResult.isFailure}")
                Log.d("UPLOAD", "value=${imageResult.getOrNull()}")
                Log.d("UPLOAD", "exception=${imageResult.exceptionOrNull()}")
                val imageKey = imageResult.getOrNull() ?: return@launch
                updateMeUseCase(
                    UpdateUserDTO(
                        name = state.value.nameInput,
                        username = state.value.user?.username,
                        bio = state.value.bioInput,
                        avatarUrl = Constants.YANDEX_STORAGE + imageKey
                    )
                ).fold(
                    onSuccess = {
                        Log.d("UPDATE ME", "SUCCESS")
                    },
                    onFailure = {
                        Log.d("UPDATE ME", "FAILURE")

                    }
                )
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}