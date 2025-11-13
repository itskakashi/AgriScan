package com.example.agriscan.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.agriscan.data.local.dao.ScanDao
import com.example.agriscan.data.local.dao.UserDao
import com.example.agriscan.data.local.entities.Scan
import com.example.agriscan.data.local.entities.User

@Database(entities = [User::class, Scan::class], version = 5)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun scanDao(): ScanDao
}
