package com.example.agriscan.data

import com.example.agriscan.domain.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uk.ac.tees.mad.agriscan.domain.util.DataError
import com.uk.ac.tees.mad.agriscan.domain.util.EmptyResult
import com.uk.ac.tees.mad.agriscan.domain.util.firebaseResult
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String,
        dob: String
    ): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            requireNotNull(user) { "firebase user was null after successful registration" }
            val userProfileData = mapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "dob" to dob,
                "uid" to user.uid,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(user.uid).set(userProfileData).await()
        }
    }

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            firebaseAuth.sendPasswordResetEmail(email).await()
        }
    }

    override suspend fun logOut(): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            firebaseAuth.signOut()
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): EmptyResult<DataError.Firebase> {
        return firebaseResult {
            val user = firebaseAuth.currentUser
            requireNotNull(user) { "User not logged in" }
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
        }
    }
}
