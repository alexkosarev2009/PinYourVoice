package com.example.shareyourvoicemapbox.ui.screens.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.data.dto.ReportMarkerDTO
import com.example.shareyourvoicemapbox.domain.report.ReportMarkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportMarkerUseCase: ReportMarkerUseCase,
    savedStateHandle: SavedStateHandle,

) : ViewModel() {
    fun selectOption(text: String) {
        _state.update {
            it.copy(selectedOption = text)
        }
    }

    fun submitReport() {
        _state.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            delay(500)
            reportMarkerUseCase.invoke(
                ReportMarkerDTO(
                    markerId = _state.value.markerId,
                    reason = _state.value.selectedOption
                )
            ).fold(
                onFailure = { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error.message ?: "")
                    }
                },
                onSuccess = {
                    _state.update {
                        it.copy(isLoading = false, isDone = true)
                    }
                }
            )
        }
    }

    private val _state = MutableStateFlow(ReportState(
        markerId = savedStateHandle.get<String>("markerId")?.toLong() ?: -1L
    ))
    val state = _state.asStateFlow()

    val options: List<Int> = listOf(
        R.string.spam,
        R.string.frauds_and_scams,
        R.string.inappropriate_content,
        R.string.misinformation,
        R.string.violence_or_graphic_content,
        R.string.hate_and_harassment,
    )
}