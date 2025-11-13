package com.example.agriscan.domain.repository

import com.example.agriscan.data.local.entities.Scan
import kotlinx.coroutines.flow.Flow

interface ScanRepository {

    suspend fun insertScan(scan: Scan)

    fun getTotalScans(): Flow<Int>

    fun getUniqueBreeds(): Flow<Int>

    fun getAllScans(): Flow<List<Scan>>

    suspend fun syncScans()

    suspend fun clearLocalScans()
}