package com.example.shareyourvoicemapbox.ui.screens.edit

import android.media.MediaMetadataRetriever
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.amplituda.ProcessAudioUseCase
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
import java.net.URLDecoder
import javax.inject.Inject

const val MAX_TITLE_LEN = 30

@HiltViewModel
class EditViewModel @Inject constructor(
    private val playAudioUseCase: PlayAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val seekToUseCase: SeekToUseCase,
    private val processAudioUseCase: ProcessAudioUseCase,
    private val getCurrentPositionUseCase: GetCurrentPositionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(EditState(
        audioPath = URLDecoder.decode(savedStateHandle["audioPath"], "UTF-8"),
    ))
    val state: StateFlow<EditState> = _state.asStateFlow()

    private val _playerState = MutableStateFlow(EditPlayerState())
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
            _state.update {
                it.copy(title = newTitle)
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
}