package com.example.agriscan.presentation.auth.login

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

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _event = Channel<LoginEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChanged -> _state.update { it.copy(email = action.email) }
            is LoginAction.OnPasswordChanged -> _state.update { it.copy(password = action.password) }
            LoginAction.OnSignInClicked -> signIn()
            LoginAction.OnSignUpClicked -> sendEvent(LoginEvent.GoToSignUp)
            LoginAction.OnForgotPasswordClicked -> sendEvent(LoginEvent.GoToForgotPassword)
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = authRepository.signIn(state.value.email, state.value.password)) {
                is Result.Success -> sendEvent(LoginEvent.Success)
                is Result.Error -> sendEvent(LoginEvent.Failure(result.error))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendEvent(event: LoginEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}