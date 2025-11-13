package com.example.agriscan.data.repository

import com.example.agriscan.data.local.dao.ScanDao
import com.example.agriscan.data.local.entities.Scan
import com.example.agriscan.domain.repository.ScanRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ScanRepositoryImpl(
    private val scanDao: ScanDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ScanRepository {

    override suspend fun insertScan(scan: Scan) {
        scanDao.insertScan(scan.copy(isSynced = false))
    }

    override fun getTotalScans(): Flow<Int> {
        return scanDao.getTotalScans()
    }

    override fun getUniqueBreeds(): Flow<Int> {
        return scanDao.getUniqueBreeds()
    }

    override fun getAllScans(): Flow<List<Scan>> {
        return scanDao.getAllScans()
    }

    override suspend fun syncScans() {
        val userId = auth.currentUser?.uid ?: return
        val scansCollection = firestore.collection("users").document(userId).collection("scans")

        CoroutineScope(Dispatchers.IO).launch {
            val unsyncedScans = scanDao.getUnsyncedScans()
            for (scan in unsyncedScans) {
                try {
                    scansCollection.document(scan.id).set(scan).await()
                    scanDao.insertScan(scan.copy(isSynced = true))
                } catch (e: Exception) {
                    // Handle exceptions
                }
            }
        }

        try {
            val serverScans = scansCollection.get().await().toObjects<Scan>()
            scanDao.insertAll(serverScans)
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    override suspend fun clearLocalScans() {
        scanDao.clearAll()
    }
}
