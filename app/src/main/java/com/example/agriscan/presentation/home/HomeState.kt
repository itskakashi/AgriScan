package com.example.agriscan.presentation.home

data class HomeState(
    val userName: String = "",
    val totalScans: Int = 0,
    val uniqueBreeds: Int = 0,
    val lastScanDate: String = "",
    val lastScanAddress: String = "",
    val lastPredictedBreed: PredictionDetails? = null,
)
