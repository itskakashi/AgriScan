package com.example.agriscan.presentation.auth.forgot_password

sealed interface ForgotPasswordAction {
    data class OnEmailChange(val email: String) : ForgotPasswordAction
    object OnSubmitClick : ForgotPasswordAction
    object OnBackToLoginClick : ForgotPasswordAction
    object OnSignUpClicked : ForgotPasswordAction
}
