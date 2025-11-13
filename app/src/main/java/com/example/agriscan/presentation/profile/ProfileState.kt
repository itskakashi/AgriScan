package com.example.agriscan.presentation.profile

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val profileImage: String = "",
    val totalScans: Int = 0,
    val uniqueBreeds: Int = 0
)
