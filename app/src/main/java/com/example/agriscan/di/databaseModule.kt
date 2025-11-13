package com.example.agriscan.di

import androidx.room.Room
import com.example.agriscan.data.local.UserDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            UserDatabase::class.java,
            "user_database"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<UserDatabase>().userDao() }
    single { get<UserDatabase>().scanDao() }
}
