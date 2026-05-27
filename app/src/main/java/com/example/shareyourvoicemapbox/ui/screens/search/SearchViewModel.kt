package com.example.shareyourvoicemapbox.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.markers.SearchMarkersByTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMarkersByTitleUseCase: SearchMarkersByTitleUseCase

) : ViewModel() {
    fun changeQuery(query: String) {
        _state.update {
            it.copy(query = query)
        }
    }

    fun search() {
        viewModelScope.launch {
            searchMarkersByTitleUseCase(_state.value.query.trim()).fold(
                onSuccess = { markers ->
                    _state.update {
                        it.copy(markers = markers)
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

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()
}