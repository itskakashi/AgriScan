package com.example.agriscan.presentation.auth.login

import com.example.agriscan.domain.util.DataError

sealed interface LoginEvent {
    object Success : LoginEvent
    data class Failure(val error: DataError.Firebase) : LoginEvent
    object GoToSignUp : LoginEvent
    object GoToForgotPassword : LoginEvent
}
