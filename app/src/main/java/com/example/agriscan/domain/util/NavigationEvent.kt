package com.example.agriscan.domain.util

sealed interface NavigationEvent {
    data class NavigateToEditHabit(val habitId: String) : NavigationEvent
    object NavigateToLogin : NavigationEvent
    object NavigateBack : NavigationEvent
}
