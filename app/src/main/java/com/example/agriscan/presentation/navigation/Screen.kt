package com.example.agriscan.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object SplashScreen : Screen

    @Serializable
    object LoginScreen : Screen

    @Serializable
    object SignUpScreen : Screen

    @Serializable
    object ForgotPasswordScreen : Screen

    @Serializable
    object HomeScreen : Screen

    @Serializable
    object LearnScreen : Screen

    @Serializable
    object TutorialScreen : Screen

    @Serializable
    object ProfileScreen : Screen

    @Serializable
    object PersonalInformationScreen : Screen

    @Serializable
    object ScanScreen : Screen

    @Serializable
    object ResultScreen : Screen
}
