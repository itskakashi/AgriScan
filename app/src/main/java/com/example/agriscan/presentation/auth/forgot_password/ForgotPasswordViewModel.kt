package com.example.agriscan.presentation.auth.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.domain.AuthRepository
import com.example.agriscan.domain.util.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<ForgotPasswordEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            ForgotPasswordAction.OnSubmitClick -> forgotPassword()
            ForgotPasswordAction.OnBackToLoginClick -> sendEvent(ForgotPasswordEvent.BackToLogin)
            ForgotPasswordAction.OnSignUpClicked -> sendEvent(ForgotPasswordEvent.GoToSignUp)
        }
    }

    private fun forgotPassword() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when(val result = authRepository.forgotPassword(state.value.email)) {
                is Result.Success -> sendEvent(ForgotPasswordEvent.Success)
                is Result.Error -> sendEvent(ForgotPasswordEvent.Failure(result.error))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendEvent(event: ForgotPasswordEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}