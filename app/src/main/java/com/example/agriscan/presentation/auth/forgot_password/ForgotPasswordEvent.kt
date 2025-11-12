package com.example.agriscan.presentation.auth.forgot_password

import com.example.agriscan.domain.util.DataError

sealed interface ForgotPasswordEvent {
    object Success : ForgotPasswordEvent
    data class Failure(val error: DataError.Firebase) : ForgotPasswordEvent
    object BackToLogin : ForgotPasswordEvent
    object GoToSignUp : ForgotPasswordEvent
}
