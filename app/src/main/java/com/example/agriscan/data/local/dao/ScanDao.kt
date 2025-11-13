package com.example.agriscan.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.agriscan.data.local.entities.Scan
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: Scan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scans: List<Scan>)

    @Query("SELECT COUNT(*) FROM scan")
    fun getTotalScans(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT breedName) FROM scan")
    fun getUniqueBreeds(): Flow<Int>

    @Query("SELECT * FROM scan")
    fun getAllScans(): Flow<List<Scan>>

    @Query("SELECT * FROM scan WHERE isSynced = 0")
    suspend fun getUnsyncedScans(): List<Scan>

    @Query("DELETE FROM scan")
    suspend fun clearAll()
}
