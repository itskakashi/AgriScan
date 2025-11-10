package com.example.agriscan.presentation.profile

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val profileImage: String = "",
    val scanCount: Int = 0,
    val predictionCount: Int = 0,
    val uniqueBreedsCount: Int = 0
)
