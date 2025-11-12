package com.example.agriscan.presentation.auth.create_account

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

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<SignUpEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.OnFullNameChanged -> _state.update { it.copy(fullName = action.fullName) }
            is SignUpAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is SignUpAction.OnPhoneChange -> _state.update { it.copy(phone = action.phone) }
            is SignUpAction.OnDobChange -> _state.update { it.copy(dob = action.dob) }
            is SignUpAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            is SignUpAction.OnConfirmPasswordChange -> _state.update { it.copy(confirmPassword = action.confirmPassword) }
            SignUpAction.OnCreateAccountClick -> signUp()
            SignUpAction.OnSignInClick -> sendEvent(SignUpEvent.GoToLogin)
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentState = state.value
            when(val result = authRepository.signUp(
                email = currentState.email,
                password = currentState.password,
                name = currentState.fullName,
                phone = currentState.phone,
                dob = currentState.dob
            )) {
                is Result.Success -> sendEvent(SignUpEvent.Success)
                is Result.Error -> sendEvent(SignUpEvent.Failure(result.error))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendEvent(event: SignUpEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}