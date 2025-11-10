package com.example.agriscan.presentation.auth.create_account

data class SignUpState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val dob: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
)
