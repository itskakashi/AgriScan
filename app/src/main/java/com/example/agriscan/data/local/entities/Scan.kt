package com.example.agriscan.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Scan(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val breedName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L,
    val address: String = "",
    val isSynced: Boolean = false
)
