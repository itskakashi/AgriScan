package com.example.agriscan.presentation.profile

import android.net.Uri

sealed interface ProfileAction {
    object EditProfile : ProfileAction
    object PersonalInformation : ProfileAction
    object Language : ProfileAction
    data class OnChangePassword(val currentPassword: String, val newPassword: String) : ProfileAction
    object Logout : ProfileAction
    data class OnProfileImageChanged(val uri: Uri) : ProfileAction
}
