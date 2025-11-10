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
                    name = "Co - 99006",
                    type = "Late Maturing",
                    region = "Andhra Pradesh, Telangana",
                    maturity = "13-14 Months",
                    juiceYield = "Very High",
                    description = "Late maturing, high-yielding variety with excellent juice quality.",
                    images = listOf(R.drawable.rice_banner, R.drawable.rice_banner),
                    keyCharacteristics = listOf(
                        "Very High sucrose content",
                        "Excellent ratooning",
                        "Resistant to major pests",
                        "Long growing period"
                    ),
                    growingTips = listOf(
                        "Plant in April",
                        "Intensive irrigation",
                        "NPK ratio: 170:85:85 kg/ha",
                        "Harvest at 13-14 months"
                    )
                ),
                // You can add more breeds here
                SugarcaneBreed(
                    name = "Co - 86032",
                    type = "Mid-late Maturing",
                    region = "Maharashtra, Karnataka",
                    maturity = "12-13 Months",
                    juiceYield = "High",
                    description = "A popular variety known for its adaptability and good commercial cane sugar.",
                    images = listOf(R.drawable.rice_banner, R.drawable.rice_banner),
                    keyCharacteristics = listOf(
                        "Good sucrose content",
                        "Tolerant to drought",
                        "Suitable for different soil types"
                    ),
                    growingTips = listOf(
                        "Planting from Oct to Feb",
                        "Requires well-drained soil",
                        "Responds well to fertilizers"
                    )
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
