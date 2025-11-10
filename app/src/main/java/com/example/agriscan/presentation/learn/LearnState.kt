package com.example.agriscan.presentation.learn

import androidx.annotation.DrawableRes

data class LearnState(
    val breeds: List<SugarcaneBreed> = emptyList()
)

data class SugarcaneBreed(
    val name: String,
    val type: String,
    val region: String,
    val maturity: String,
    val juiceYield: String,
    val description: String,
    @DrawableRes val images: List<Int>,
    val keyCharacteristics: List<String>,
    val growingTips: List<String>
)
