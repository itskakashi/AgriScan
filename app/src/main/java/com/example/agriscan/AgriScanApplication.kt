package com.example.agriscan

import android.app.Application
import com.example.agriscan.di.appModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AgriScanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            androidContext(this@AgriScanApplication)
            modules(appModule)
        }
    }
}
