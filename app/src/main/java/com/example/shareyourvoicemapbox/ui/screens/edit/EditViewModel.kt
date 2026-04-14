package com.example.shareyourvoicemapbox.ui.screens.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.amplituda.ProcessAudioUseCase
import com.example.shareyourvoicemapbox.domain.player.PlayAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

const val MAX_TITLE_LEN = 30

@HiltViewModel
class EditViewModel @Inject constructor(
    private val playAudioUseCase: PlayAudioUseCase,
    private val processAudioUseCase: ProcessAudioUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(EditState(
        audioPath = URLDecoder.decode(savedStateHandle["audioPath"], "UTF-8"),
    ))
    val state: StateFlow<EditState> = _state.asStateFlow()


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

}