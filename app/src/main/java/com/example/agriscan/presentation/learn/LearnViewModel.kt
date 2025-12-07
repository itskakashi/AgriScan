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
                    images = listOf(R.drawable.sugarcane_home, R.drawable.sugarcane_home),
                    keyCharacteristics = R.array.breed_1_key_characteristics,
                    growingTips = R.array.breed_1_growing_tips
                ),
                SugarcaneBreed(
                    name = R.string.breed_2_name,
                    type = R.string.breed_2_type,
                    region = R.string.breed_2_region,
                    maturity = R.string.breed_2_maturity,
                    juiceYield = R.string.breed_2_juice_yield,
                    description = R.string.breed_2_description,
                    images = listOf(R.drawable.sugarcane_home, R.drawable.sugarcane_home),
                    keyCharacteristics = R.array.breed_2_key_characteristics,
                    growingTips = R.array.breed_2_growing_tips
                ),
                SugarcaneBreed(
                    name = R.string.breed_3_name,
                    type = R.string.breed_3_type,
                    region = R.string.breed_3_region,
                    maturity = R.string.breed_3_maturity,
                    juiceYield = R.string.breed_3_juice_yield,
                    description = R.string.breed_3_description,
                    images = listOf(R.drawable.colk_12209, R.drawable.colk_12209),
                    keyCharacteristics = R.array.breed_3_key_characteristics,
                    growingTips = R.array.breed_3_growing_tips
                ),
                SugarcaneBreed(
                    name = R.string.breed_4_name,
                    type = R.string.breed_4_type,
                    region = R.string.breed_4_region,
                    maturity = R.string.breed_4_maturity,
                    juiceYield = R.string.breed_4_juice_yield,
                    description = R.string.breed_4_description,
                    images = listOf(R.drawable.colk_14201, R.drawable.colk_14201),
                    keyCharacteristics = R.array.breed_4_key_characteristics,
                    growingTips = R.array.breed_4_growing_tips
                ),
                SugarcaneBreed(
                    name = R.string.breed_5_name,
                    type = R.string.breed_5_type,
                    region = R.string.breed_5_region,
                    maturity = R.string.breed_5_maturity,
                    juiceYield = R.string.breed_5_juice_yield,
                    description = R.string.breed_5_description,
                    images = listOf(R.drawable.colk_15466, R.drawable.colk_15466),
                    keyCharacteristics = R.array.breed_5_key_characteristics,
                    growingTips = R.array.breed_5_growing_tips
                ),
                SugarcaneBreed(
                    name = R.string.breed_6_name,
                    type = R.string.breed_6_type,
                    region = R.string.breed_6_region,
                    maturity = R.string.breed_6_maturity,
                    juiceYield = R.string.breed_6_juice_yield,
                    description = R.string.breed_6_description,
                    images = listOf(R.drawable.colk_16466, R.drawable.colk_16466),
                    keyCharacteristics = R.array.breed_6_key_characteristics,
                    growingTips = R.array.breed_6_growing_tips
                ),
                SugarcaneBreed(
                    name = R.string.breed_7_name,
                    type = R.string.breed_7_type,
                    region = R.string.breed_7_region,
                    maturity = R.string.breed_7_maturity,
                    juiceYield = R.string.breed_7_juice_yield,
                    description = R.string.breed_7_description,
                    images = listOf(R.drawable.colk_16470, R.drawable.colk_16470),
                    keyCharacteristics = R.array.breed_7_key_characteristics,
                    growingTips = R.array.breed_7_growing_tips
                ),
                SugarcaneBreed(
                    name = R.string.breed_8_name,
                    type = R.string.breed_8_type,
                    region = R.string.breed_8_region,
                    maturity = R.string.breed_8_maturity,
                    juiceYield = R.string.breed_8_juice_yield,
                    description = R.string.breed_8_description,
                    images = listOf(R.drawable.colk_94184, R.drawable.colk_94184),
                    keyCharacteristics = R.array.breed_8_key_characteristics,
                    growingTips = R.array.breed_8_growing_tips
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
