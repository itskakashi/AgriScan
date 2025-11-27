package com.example.agriscan.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.UserRepository
import com.example.agriscan.domain.repository.ScanRepository
import com.example.agriscan.util.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val languageManager: LanguageManager,
    private val scanRepository: ScanRepository,
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
        viewModelScope.launch {
            scanRepository.getTotalScans().collectLatest { totalScans ->
                _uiState.update { it.copy(totalScans = totalScans) }
            }
        }
        viewModelScope.launch {
            scanRepository.getUniqueBreeds().collectLatest { uniqueBreeds ->
                _uiState.update { it.copy(uniqueBreeds = uniqueBreeds) }
            }
        }
        viewModelScope.launch {
            scanRepository.getAllScans()
                .map { scans ->
                    // Ensure stable sorting if the database doesn't guarantee order
                    scans.maxByOrNull { it.timestamp }
                }
                .distinctUntilChanged()
                .collectLatest { lastScan ->
                    val breedName = lastScan?.breedName ?: ""
                    val date = lastScan?.timestamp?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.format(Date(it))
                    } ?: ""
                    _uiState.update { it.copy(
                        lastPredictedBreed = breedName,
                        lastScanDate = date,
                        lastScanAddress = lastScan?.address ?: ""
                    ) }
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

    fun syncScans() {
        viewModelScope.launch {
            scanRepository.syncScans()
        }
    }
}
