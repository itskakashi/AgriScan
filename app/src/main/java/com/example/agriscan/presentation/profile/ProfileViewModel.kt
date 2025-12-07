package com.example.agriscan.presentation.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.UserRepository
import com.example.agriscan.domain.AuthRepository
import com.example.agriscan.domain.repository.ScanRepository
import com.example.agriscan.presentation.home.PredictionDetails
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        refresh()
    }

    fun refresh() {
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
        viewModelScope.launch {
            scanRepository.getAllScans()
                .map { scans ->
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

                    _state.update { it.copy(
                        lastPredictedBreed = details,
                        lastScanDate = date,
                    ) }
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
