package com.example.agriscan.presentation.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.util.LanguageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TutorialViewModel(private val languageManager: LanguageManager) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorialState())
    val uiState = _uiState.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TutorialState()
        )

    fun onAction(action: TutorialAction) {
        when (action) {
            is TutorialAction.PlayVideo -> {
                _uiState.update { it.copy(isPlaying = true) }
            }
            is TutorialAction.PauseVideo -> {
                _uiState.update { it.copy(isPlaying = false) }
            }
            is TutorialAction.SetVideo -> {
                _uiState.update { it.copy(youtubeVideoId = action.videoId, isPlaying = true) }
            }
            TutorialAction.LanguageChangeTapped -> {
                viewModelScope.launch {
                    val currentLanguage = languageManager.language.value
                    val newLanguage = if (currentLanguage == "en") "hi" else "en"
                    languageManager.setLanguage(newLanguage)
                }
            }
        }
    }
}
