package com.example.agriscan.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PersonalInformationViewModel(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val _state = MutableStateFlow(PersonalInformationState())
    val state = _state.asStateFlow()

    init {
        firebaseAuth.currentUser?.let {
            viewModelScope.launch {
                userRepository.syncUser()
                userRepository.getUser(it.uid).collectLatest { user ->
                    user?.let {
                        _state.value = state.value.copy(
                            name = user.name,
                            email = user.email,
                            phone = user.phone,
                            dob = user.dob
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: PersonalInformationAction) {
        when (action) {
            is PersonalInformationAction.OnNameChanged -> {
                _state.value = state.value.copy(name = action.name)
            }
            is PersonalInformationAction.OnEmailChanged -> {
                _state.value = state.value.copy(email = action.email)
            }
            is PersonalInformationAction.OnPhoneChanged -> {
                _state.value = state.value.copy(phone = action.phone)
            }
            is PersonalInformationAction.OnDobChanged -> {
                _state.value = state.value.copy(dob = action.dob)
            }
            PersonalInformationAction.SaveChanges -> {
                viewModelScope.launch {
                    firebaseAuth.currentUser?.let { user ->
                        val updatedUser = state.value.copy()
                        userRepository.updateUser(user.uid, updatedUser)
                    }
                }
            }
        }
    }
}

data class PersonalInformationState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dob: String = "",
)

sealed interface PersonalInformationAction {
    data class OnNameChanged(val name: String) : PersonalInformationAction
    data class OnEmailChanged(val email: String) : PersonalInformationAction
    data class OnPhoneChanged(val phone: String) : PersonalInformationAction
    data class OnDobChanged(val dob: String) : PersonalInformationAction
    object SaveChanges : PersonalInformationAction
}
