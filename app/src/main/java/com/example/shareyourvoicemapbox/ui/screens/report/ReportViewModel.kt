package com.example.shareyourvoicemapbox.ui.screens.report

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ReportViewModel : ViewModel() {
    fun selectOption(text: String) {
        _state.update {
            it.copy(selectedOption = text)
        }
    }

    private val _state = MutableStateFlow(ReportState())
    val state = _state.asStateFlow()

    val options = listOf(
        "Spam",
        "Frauds and scams",
        "Inappropriate content",
        "Misinformation",
        "Violence or graphic content",
        "Hate and harassment"
        )
}