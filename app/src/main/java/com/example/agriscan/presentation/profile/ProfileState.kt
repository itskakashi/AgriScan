package com.example.agriscan.presentation.profile

import com.example.agriscan.presentation.home.PredictionDetails

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val profileImage: String = "",
    val totalScans: Int = 0,
    val uniqueBreeds: Int = 0,
    val lastPredictedBreed: PredictionDetails? = null,
    val lastScanDate: String = "",
)
