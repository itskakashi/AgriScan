package com.example.agriscan.di

import androidx.room.Room
import com.example.agriscan.data.AuthRepositoryImpl
import com.example.agriscan.data.UserRepository
import com.example.agriscan.data.local.UserDatabase
import com.example.agriscan.data.repository.ClassificationRepositoryImpl
import com.example.agriscan.data.repository.GeocodingRepositoryImpl
import com.example.agriscan.data.repository.ScanRepositoryImpl
import com.example.agriscan.domain.AuthRepository
import com.example.agriscan.domain.repository.ClassificationRepository
import com.example.agriscan.domain.repository.GeocodingRepository
import com.example.agriscan.domain.repository.ScanRepository
import com.example.agriscan.presentation.auth.create_account.SignUpViewModel
import com.example.agriscan.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.example.agriscan.presentation.auth.login.LoginViewModel
import com.example.agriscan.presentation.home.HomeViewModel
import com.example.agriscan.presentation.learn.LearnViewModel
import com.example.agriscan.presentation.profile.PersonalInformationViewModel
import com.example.agriscan.presentation.profile.ProfileViewModel
import com.example.agriscan.presentation.result.ResultViewModel
import com.example.agriscan.presentation.scan.ScanViewModel
import com.example.agriscan.presentation.tutorial.TutorialViewModel
import com.example.agriscan.util.LanguageManager
import com.example.agriscan.util.LocationTracker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }
        }
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            UserDatabase::class.java,
            "user_database"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<UserDatabase>().userDao() }
    single { get<UserDatabase>().scanDao() }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single { UserRepository(get(), get(), get(), get()) }
    single<ScanRepository> { ScanRepositoryImpl(get(), get(), get()) }
    single<ClassificationRepository> { ClassificationRepositoryImpl(get()) }
    single<GeocodingRepository> { GeocodingRepositoryImpl(get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { LocationTracker(androidContext()) }

    single {
        createSupabaseClient(
            supabaseUrl = "https://kvdagjrceetbstkqanlf.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt2ZGFnanJjZWV0YnN0a3FhbmxmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA1NTgwODQsImV4cCI6MjA3NjEzNDA4NH0.7S3oqGekCWggEMtaCne9JknjhqlGdpcxFZvUEurmRGI"
        ) {
            install(Storage)
        }
    }
    single { get<SupabaseClient>().storage }
    single { LanguageManager(androidContext()) }

    viewModel { LoginViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { PersonalInformationViewModel(get(), get()) }
    viewModel { LearnViewModel(get()) }
    viewModel { TutorialViewModel(get()) }
    viewModel { ScanViewModel(get(), get(), get(), get()) }
    viewModel { params -> ResultViewModel(savedStateHandle = params.get()) }
}
