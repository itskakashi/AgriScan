package com.example.agriscan.presentation.auth.create_account

sealed interface SignUpAction {
    data class OnFullNameChanged(val fullName: String) : SignUpAction
    data class OnEmailChange(val email: String) : SignUpAction
    data class OnPhoneChange(val phone: String) : SignUpAction
    data class OnDobChange(val dob: String) : SignUpAction
    data class OnPasswordChange(val password: String) : SignUpAction
    data class OnConfirmPasswordChange(val confirmPassword: String) : SignUpAction
    object OnCreateAccountClick : SignUpAction
    object OnSignInClick : SignUpAction
}
