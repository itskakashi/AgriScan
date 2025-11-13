package com.example.agriscan.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val dob: String,
    val profilePictureUrl: String
)
