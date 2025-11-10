package com.example.agriscan.presentation.result

data class ResultState(
    val breedName: String = "Sugarcane Breed",
    val confidence: Float = 0.9f,
    val predictionDate: String = "2024-07-28",
    val imageUrl: String? = null
)
