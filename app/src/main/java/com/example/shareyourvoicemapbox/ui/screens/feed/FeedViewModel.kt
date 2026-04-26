package com.example.shareyourvoicemapbox.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyourvoicemapbox.domain.markers.GetMarkersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getMarkersUseCase: GetMarkersUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(FeedState())
    val uiState: StateFlow<FeedState> = _uiState.asStateFlow()

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

}