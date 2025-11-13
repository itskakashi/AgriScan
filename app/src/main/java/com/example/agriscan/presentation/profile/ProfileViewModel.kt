package com.example.agriscan.presentation.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.UserRepository
import com.example.agriscan.domain.AuthRepository
import com.example.agriscan.domain.repository.ScanRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository, private val firebaseAuth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileState()
    )

    init {
        loadUserData()
        viewModelScope.launch {
            scanRepository.getTotalScans().collectLatest { totalScans ->
                _state.update { it.copy(totalScans = totalScans) }
            }
        }
        viewModelScope.launch {
            scanRepository.getUniqueBreeds().collectLatest { uniqueBreeds ->
                _state.update { it.copy(uniqueBreeds = uniqueBreeds) }
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            firebaseAuth.currentUser?.uid?.let { uid ->
                userRepository.getUser(uid).collectLatest { user ->
                    _state.update {
                        it.copy(
                            userName = user?.name ?: "",
                            userEmail = user?.email ?: "",
                            profileImage = user?.profilePictureUrl ?: "",
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            userRepository.syncUser()
        }
    }

    fun onAction(action: ProfileAction, context: Context) {
        when (action) {
            is ProfileAction.EditProfile -> {
                // TODO: Handle edit profile
            }
            is ProfileAction.PersonalInformation -> {
                // TODO: Handle personal information
            }
            is ProfileAction.Language -> {
                // TODO: Handle language change
            }
            is ProfileAction.OnChangePassword -> {
                viewModelScope.launch {
                    authRepository.changePassword(action.currentPassword, action.newPassword)
                }
            }
            is ProfileAction.Logout -> {
             viewModelScope.launch {
                 authRepository.logOut()
                 scanRepository.clearLocalScans()
             }
            }
            is ProfileAction.OnProfileImageChanged -> {
                viewModelScope.launch {
                    userRepository.uploadProfilePicture(action.uri, context)
                }
            }
        }
    }
}
