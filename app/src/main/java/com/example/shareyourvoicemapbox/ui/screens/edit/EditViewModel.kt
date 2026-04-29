package com.example.shareyourvoicemapbox.ui.screens.edit

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.data.dto.CreateMarkerDTO
import com.example.shareyourvoicemapbox.domain.UploadFileUseCase
import com.example.shareyourvoicemapbox.domain.amplituda.ProcessAudioUseCase
import com.example.shareyourvoicemapbox.domain.markers.CreateMarkerUseCase
import com.example.shareyourvoicemapbox.domain.player.GetCurrentPositionUseCase
import com.example.shareyourvoicemapbox.domain.player.PauseAudioUseCase
import com.example.shareyourvoicemapbox.domain.player.PlayAudioUseCase
import com.example.shareyourvoicemapbox.domain.player.SeekToUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLDecoder
import java.time.Instant
import javax.inject.Inject

const val MAX_TITLE_LEN = 30

@HiltViewModel
class EditViewModel @Inject constructor(
    private val playAudioUseCase: PlayAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val seekToUseCase: SeekToUseCase,
    private val processAudioUseCase: ProcessAudioUseCase,
    private val getCurrentPositionUseCase: GetCurrentPositionUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val createMarkerUseCase: CreateMarkerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isPinYourVoiceEnabled: Boolean
        get() = _state.value.title.trim().length > 3  && _state.value.imageUri != null

    private val _state = MutableStateFlow(
        EditState(
            audioPath = URLDecoder.decode(savedStateHandle["audioPath"] ?: "", "UTF-8"),
            lat = savedStateHandle.get<String>("lat")?.toDoubleOrNull() ?: 0.0,
            lng = savedStateHandle.get<String>("lng")?.toDoubleOrNull() ?: 0.0
        )
    )
    val state: StateFlow<EditState> = _state.asStateFlow()

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private var timerJob: Job? = null


    fun processAudio() {
        viewModelScope.launch {
            processAudioUseCase(_state.value.audioPath).fold(
                onSuccess = { amplitudes ->
                    _state.update {
                        it.copy(amplitudes = amplitudes)
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

    fun onTitleChange(newTitle: String) {
        if (newTitle.length <= MAX_TITLE_LEN) {
            _state.update { state ->
                state.copy(title = newTitle.filter { it.isLetterOrDigit() })
            }
        }
    }

    fun playAudio() {
        if (!_playerState.value.isPlaying) {
            playAudioUseCase(_state.value.audioPath)
            _playerState.update {
                it.copy(isPlaying = true)
            }
        }
        resumeTimer()
    }

    fun pauseAudio() {
        if (_playerState.value.isPlaying) {
            pauseAudioUseCase()
            _playerState.update {
                it.copy(isPlaying = false)
            }
        }
        pauseTimer()

    }
    fun seekTo(ms: Long) {
        seekToUseCase(ms)
    }
    fun onWaveformProgressChange(progress: Float) {
        val duration = playerState.value.maxDuration
        val newPosition = (progress * duration).toLong()

        seekTo(newPosition)
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun resumeTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (isActive) {

                val current = try {
                    getCurrentPositionUseCase()
                } catch (e: Exception) {
                    0
                }

                val duration = _playerState.value.maxDuration

                _playerState.update {
                    it.copy(
                        currentPosition = current)
                }

                if (duration in 1..current) {
                    pauseAudio()
                    seekTo(0)
                    break
                }

                delay(33)
            }
        }
    }

    fun getAudioDurationMs(path: String) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
            val durationStr = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )
            _playerState.update {
                it.copy(maxDuration = durationStr?.toLong() ?: 0L)
            }
        } catch (e: Exception) {

        } finally {
            retriever.release()
        }
    }

    fun onImagePicked(uri: Uri) {
        _state.update {
            it.copy(imageUri = uri)
        }
    }

    fun onDeleteImage() {
        _state.update {
            it.copy(imageUri = null)
        }
    }

    fun onPostClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000)

            val audioResult = uploadFileUseCase(
                fileName = "audio.mp3",
                contentType = "audio/mpeg",
                filePath = _state.value.audioPath
            ).onFailure {
                _state.update {
                    it.copy(isLoading = false,
                        error = "Failed to upload audio")
                }
                return@launch
            }

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

            val audioKey = audioResult.getOrNull()
            val imageKey = imageResult.getOrNull()

            if (audioKey == null || imageKey == null) {
                return@launch
            }

            createMarkerUseCase(
                CreateMarkerDTO(
                    title = _state.value.title,
                    lat = _state.value.lat,
                    lng = _state.value.lng,
                    imageUrl = imageKey,
                    audioUrl = audioKey,
                )
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(isLoading = false,
                             isDone = true)
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(isLoading = false,
                            error = error.message ?: "")
                    }
                }
            )
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
    fun clearError() {
        _state.update {
            it.copy(error = "")
        }
    }
}