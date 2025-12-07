package com.example.agriscan.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.UserRepository
import com.example.agriscan.domain.repository.ScanRepository
import com.example.agriscan.translator.Translator
import com.example.agriscan.util.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
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
    private val translator: Translator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            languageManager.language.drop(1).collectLatest {
                refresh()
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

    fun refresh() {
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                userRepository.getUser(uid).collectLatest { user ->
                    _uiState.update { it.copy(userName = translator.translate(user?.name ?: "", languageManager.language.value)) }
                }
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

                    val details = parsePredictionText(breedName)
                    val translatedDetails = details.copy(
                        detected = translator.translate(details.detected, languageManager.language.value),
                        breed = translator.translate(details.breed, languageManager.language.value),
                        confidence = translator.translate(details.confidence, languageManager.language.value),
                    )

                    val translatedDate = translator.translate(date, languageManager.language.value)
                    val translatedAddress = translator.translate(lastScan?.address ?: "", languageManager.language.value)


                    _uiState.update {
                        it.copy(
                            lastPredictedBreed = translatedDetails,
                            lastScanDate = translatedDate,
                            lastScanAddress = translatedAddress
                        )
                    }
                }
        }
    }

    private fun parsePredictionText(rawText: String): PredictionDetails {
        if (rawText.isBlank()) {
            return PredictionDetails()
        }

        val containsDetails = rawText.contains("✅") || rawText.contains("🧠") || rawText.contains("📊")

        if (!containsDetails) {
            return PredictionDetails(breed = rawText.trim())
        }

        val parts = rawText.split(Regex("(?=✅)|(?=🧠)|(?=📊)"))

        var detected = ""
        var breed = ""
        var confidence = ""

        for (part in parts) {
            if (part.isBlank()) continue

            val trimmedPart = part.trim()
            when {
                trimmedPart.startsWith("✅") -> detected = trimmedPart.substring(1).trim()
                trimmedPart.startsWith("🧠") -> breed = trimmedPart.substring(1).trim()
                trimmedPart.startsWith("📊") -> confidence = trimmedPart.substring(1).trim()
            }
        }
        return PredictionDetails(detected, breed, confidence)
    }
}
