package com.example.shareyourvoicemapbox.ui.screens.feed

import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersUseCase
import com.example.shareyourvoicemapbox.domain.player.exo.GetCurrentExoPositionUseCase
import com.example.shareyourvoicemapbox.domain.player.exo.PauseExoAudioUseCase
import com.example.shareyourvoicemapbox.domain.player.exo.PlayExoAudioUseCase
import com.example.shareyourvoicemapbox.domain.player.exo.ResumeExoAudioUseCase
import com.example.shareyourvoicemapbox.domain.player.exo.SeekExoToUseCase
import com.example.shareyourvoicemapbox.ui.screens.edit.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getMarkersUseCase: GetMarkersUseCase,
    private val playExoAudioUseCase: PlayExoAudioUseCase,
    private val resumeExoAudioUseCase: ResumeExoAudioUseCase,
    private val pauseExoAudioUseCase: PauseExoAudioUseCase,
    private val getCurrentExoPositionUseCase: GetCurrentExoPositionUseCase,
    private val seekExoToUseCase: SeekExoToUseCase,
): ViewModel() {

    private val _uiState = MutableStateFlow(FeedState())
    val uiState: StateFlow<FeedState> = _uiState.asStateFlow()

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var timerJob: Job? = null


    init {
        getData()
    }

    fun getData() {
        _uiState.update {
            it.copy(isRefreshing = true)
        }
        viewModelScope.launch {
            getMarkersUseCase().fold(
                onSuccess = { data ->
                    _uiState.update {
                        it.copy(markers = data, isRefreshing = false)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "", isRefreshing = false)
                    }
                }
            )
        }
    }

    fun viewPublic() {
        _uiState.update {
            it.copy(isViewingPublic = true)
        }
    }
    fun viewFriends() {
        _uiState.update {
            it.copy(isViewingPublic = false)
        }
    }

    fun playAudio(url: String, id: Int) {
        if (_uiState.value.currentAudioUrl != url) {
            pauseAudio()
            playExoAudioUseCase(url)
            _playerState.update {
                it.copy(isPlaying = true)
            }
            _uiState.update {
                it.copy(currentAudioUrl = url, currentPlayingId = id)
            }
        }
        else {
            if (_playerState.value.isPlaying) {
                pauseAudio()
            } else {
                resumeExoAudioUseCase()
                _playerState.update {
                    it.copy(isPlaying = true)
                }
                _uiState.update {
                    it.copy(currentAudioUrl = url, currentPlayingId = id)
                }
            }
        }
        resumeTimer()
    }
    fun pauseAudio() {
        if (_playerState.value.isPlaying) {
            pauseExoAudioUseCase()
            _playerState.update {
                it.copy(isPlaying = false)
            }
        }
        pauseTimer()
    }


    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun resumeTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (isActive) {
                val current = try {
                    getCurrentExoPositionUseCase()
                } catch (e: Exception) {
                    0
                }

                val duration = _playerState.value.maxDuration

                _playerState.update {
                    it.copy(
                        currentPosition = current.toInt())
                }

                if (duration in 1..current) {
                    pauseExoAudioUseCase()
                    seekExoToUseCase(0)
                    _playerState.update {
                        it.copy(isPlaying = false)
                    }
                    break
                }

                delay(100)
            }
        }
    }

    fun onWaveformProgressChange(progress: Float) {
        val duration = playerState.value.maxDuration
        val newPosition = (progress * duration).toLong()

        seekExoToUseCase(newPosition)
    }

    suspend fun getAudioDurationMs(url: String) {
        withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(url, HashMap())

                val durationStr = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )
                Log.d("DURATION", durationStr.toString())

                _playerState.update {
                    it.copy(maxDuration = durationStr?.toLong() ?: 0L)
                }

            } catch (e: Exception) {
                _playerState.update {
                    it.copy(maxDuration = 0L)
                }
            } finally {
                retriever.release()
            }
        }
    }

}