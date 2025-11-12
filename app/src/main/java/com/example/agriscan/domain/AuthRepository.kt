package com.example.agriscan.domain

import com.example.agriscan.domain.util.DataError
import com.example.agriscan.domain.util.EmptyResult

interface AuthRepository {
    suspend fun signIn(email: String, password: String): EmptyResult<DataError.Firebase>
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String,
        dob: String
    ): EmptyResult<DataError.Firebase>
    suspend fun forgotPassword(email: String): EmptyResult<DataError.Firebase>
    suspend fun logOut(): EmptyResult<DataError.Firebase>
    suspend fun changePassword(currentPassword: String, newPassword: String): EmptyResult<DataError.Firebase>
}
