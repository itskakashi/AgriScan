package com.example.agriscan.presentation.auth.forgot_password

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false
)
