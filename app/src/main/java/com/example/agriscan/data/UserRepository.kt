package com.example.agriscan.data

import android.content.Context
import android.net.Uri
import com.example.agriscan.data.local.User
import com.example.agriscan.data.local.UserDao
import com.example.agriscan.presentation.profile.PersonalInformationState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserRepository(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: Storage
) {

    fun getUser(uid: String): Flow<User?> = userDao.getUser(uid)

    suspend fun syncUser() {
        firebaseAuth.currentUser?.let { firebaseUser ->
            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
            val user = User(
                uid = firebaseUser.uid,
                name = userDoc.getString("name") ?: "",
                email = userDoc.getString("email") ?: "",
                phone = userDoc.getString("phone") ?: "",
                dob = userDoc.getString("dob") ?: "",
                profilePictureUrl = userDoc.getString("profilePictureUrl") ?: ""
            )
            userDao.insertOrUpdateUser(user)
        }
    }

    suspend fun updateUser(uid: String, updatedUser: PersonalInformationState) {
        val userMap = mapOf(
            "name" to updatedUser.name,
            "phone" to updatedUser.phone,
            "dob" to updatedUser.dob
        )
        firestore.collection("users").document(uid).update(userMap).await()
        syncUser()
    }

    suspend fun uploadProfilePicture(uri: Uri, context: Context) {
        firebaseAuth.currentUser?.let { firebaseUser ->
            val fileName = "${firebaseUser.uid}-${UUID.randomUUID()}"
            val fileBytes = context.contentResolver.openInputStream(uri)?.readBytes()
            fileBytes?.let {
                val bucket = storage["satyam"]
                bucket.upload(fileName, it)
                val publicUrl = bucket.publicUrl(fileName)
                firestore.collection("users").document(firebaseUser.uid).update("profilePictureUrl", publicUrl).await()
                syncUser()
            }
        }
    }
}
