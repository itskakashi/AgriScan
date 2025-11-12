package com.example.agriscan.presentation.auth.create_account

import com.example.agriscan.domain.util.DataError

sealed interface SignUpEvent {
    object Success : SignUpEvent
    data class Failure(val error: DataError) : SignUpEvent
    object GoToLogin : SignUpEvent
}
