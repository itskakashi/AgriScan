package com.example.agriscan.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.UserRepository
import com.example.agriscan.util.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                userRepository.getUser(uid).collectLatest { user ->
                    _uiState.update { it.copy(userName = user?.name ?: "") }
                }
            }
        }
        viewModelScope.launch {
            languageManager.language.collectLatest {
                // Re-fetch or update any language-dependent data here if necessary
            }
        }
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.NavItemTapped -> {
                // TODO: Handle navigation
            }
            HomeAction.ScanButtonTapped -> {
                // TODO: Handle scan
            }
            HomeAction.SupportButtonTapped -> {
                // TODO: Handle support
            }

            HomeAction.LanguageChangeTapped -> {
                viewModelScope.launch {
                    val currentLanguage = languageManager.language.value
                    val newLanguage = if (currentLanguage == "en") "hi" else "en"
                    languageManager.setLanguage(newLanguage)
                }
            }
        }
    }
}
