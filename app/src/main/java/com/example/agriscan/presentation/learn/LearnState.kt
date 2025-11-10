package com.example.agriscan.presentation.learn

import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class LearnState(
    val breeds: List<SugarcaneBreed> = emptyList()
)

data class SugarcaneBreed(
    @StringRes val name: Int,
    @StringRes val type: Int,
    @StringRes val region: Int,
    @StringRes val maturity: Int,
    @StringRes val juiceYield: Int,
    @StringRes val description: Int,
    @DrawableRes val images: List<Int>,
    @ArrayRes val keyCharacteristics: Int,
    @ArrayRes val growingTips: Int
)
