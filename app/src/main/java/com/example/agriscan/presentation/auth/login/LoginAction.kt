package com.example.agriscan.presentation.auth.login

sealed interface LoginAction {
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    object OnSignInClicked : LoginAction
    object OnSignUpClicked : LoginAction
    object OnForgotPasswordClicked : LoginAction
}
