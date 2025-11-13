package com.example.agriscan.presentation.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.local.Location
import com.example.agriscan.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state = _state.asStateFlow()
        .stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000L),
            ResultState()
        )

    init {
        val result = savedStateHandle.get<String>("result")
        val address = savedStateHandle.get<String>("address")
        _state.value = ResultState(result = result, address = address)
    }

    fun onAction(action: ResultAction) {
        when (action) {
            is ResultAction.DisableButtons -> {
                _state.update { it.copy(buttonsEnabled = false) }
            }
        }
    }
}
