package com.example.agriscan.presentation.home

import java.util.Date

data class HomeState(
    val userName: String = "",
    val totalScans: Int = 0,
    val uniqueBreeds: Int = 0,
    val lastPredictedBreed: String = "",
    val lastScanDate: String = "",
    val lastScanAddress: String = ""
)
