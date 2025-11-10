package com.example.agriscan.presentation.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.R
import com.example.agriscan.util.LanguageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LearnViewModel(private val languageManager: LanguageManager) : ViewModel() {

    private val _state = MutableStateFlow(
        LearnState(
            breeds = listOf(
                SugarcaneBreed(
                    name = R.string.breed_1_name,
                    type = R.string.breed_1_type,
                    region = R.string.breed_1_region,
                    maturity = R.string.breed_1_maturity,
                    juiceYield = R.string.breed_1_juice_yield,
                    description = R.string.breed_1_description,
                    images = listOf(R.drawable.rice_banner, R.drawable.rice_banner),
                    keyCharacteristics = R.array.breed_1_key_characteristics,
                    growingTips = R.array.breed_1_growing_tips
                ),
                // You can add more breeds here
                SugarcaneBreed(
                    name = R.string.breed_2_name,
                    type = R.string.breed_2_type,
                    region = R.string.breed_2_region,
                    maturity = R.string.breed_2_maturity,
                    juiceYield = R.string.breed_2_juice_yield,
                    description = R.string.breed_2_description,
                    images = listOf(R.drawable.rice_banner, R.drawable.rice_banner),
                    keyCharacteristics = R.array.breed_2_key_characteristics,
                    growingTips = R.array.breed_2_growing_tips
                )
            )
        )
    )
    val state = _state.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LearnState()
        )

    fun onAction(action: LearnAction) {
        when (action) {
            LearnAction.LanguageChangeTapped -> {
                viewModelScope.launch {
                    val currentLanguage = languageManager.language.value
                    val newLanguage = if (currentLanguage == "en") "hi" else "en"
                    languageManager.setLanguage(newLanguage)
                }
            }
        }
    }
}
