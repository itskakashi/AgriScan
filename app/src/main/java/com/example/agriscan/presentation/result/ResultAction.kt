package com.example.agriscan.presentation.result

sealed interface ResultAction {
    object DisableButtons : ResultAction
}