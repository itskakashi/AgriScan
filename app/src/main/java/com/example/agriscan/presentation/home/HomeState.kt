package com.example.agriscan.presentation.home

data class HomeState(
    val userName: String = "",
    val lastPredictedDate: String = "2024-07-28",
    val lastPredictedBreed: String = "Co - 990",
    val numberOfScans: String = "45",
    val numberOfPredictions: String = "15",
    val uniqueBreeds: String = "16",
    val breedLocation: String = "Bhopal (M.P)"
)
