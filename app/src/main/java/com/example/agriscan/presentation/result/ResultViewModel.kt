package com.example.agriscan.presentation.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.local.Location
import com.example.agriscan.domain.util.Result
import com.example.agriscan.translator.Translator
import com.example.agriscan.util.LanguageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class ResultViewModel(
    savedStateHandle: SavedStateHandle,
    private val translator: Translator,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state = _state.asStateFlow()
        .stateIn(
            viewModelScope,
            kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000L),
            ResultState()
        )

    init {
        val result = savedStateHandle.get<String>("result") ?: ""
        val encodedAddress = savedStateHandle.get<String>("address") ?: ""
        val address = URLDecoder.decode(encodedAddress, StandardCharsets.UTF_8.toString())

        viewModelScope.launch {
            val translatedResult = translator.translate(result, languageManager.language.value)
            val translatedAddress = translator.translate(address, languageManager.language.value)
            _state.value = ResultState(result = translatedResult, address = translatedAddress)
        }
    }

    fun onAction(action: ResultAction) {
        when (action) {
            is ResultAction.DisableButtons -> {
                _state.update { it.copy(buttonsEnabled = false) }
            }
        }
    }
}
