package com.example.agriscan.presentation.home

sealed class HomeAction {
    data class NavItemTapped(val route: String) : HomeAction()
    object ScanButtonTapped : HomeAction()
    object SupportButtonTapped : HomeAction()
    object LanguageChangeTapped : HomeAction()
}
