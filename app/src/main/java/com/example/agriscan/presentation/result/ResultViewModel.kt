package com.example.agriscan.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class ResultViewModel : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state = _state.asStateFlow()
        .stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000L),
            ResultState()
        )

    fun onAction(action: ResultAction) {
        // No actions for now
    }
}
