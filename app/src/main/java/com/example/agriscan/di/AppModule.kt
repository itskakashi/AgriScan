package com.example.agriscan.di

import com.example.agriscan.data.AuthRepositoryImpl
import com.example.agriscan.data.UserRepository
import com.example.agriscan.domain.AuthRepository
import com.example.agriscan.presentation.auth.create_account.SignUpViewModel
import com.example.agriscan.presentation.auth.forgot_password.ForgotPasswordViewModel
import com.example.agriscan.presentation.auth.login.LoginViewModel
import com.example.agriscan.presentation.home.HomeViewModel
import com.example.agriscan.presentation.learn.LearnViewModel
import com.example.agriscan.presentation.profile.PersonalInformationViewModel
import com.example.agriscan.presentation.profile.ProfileViewModel
import com.example.agriscan.presentation.tutorial.TutorialViewModel
import com.example.agriscan.util.LanguageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    includes(databaseModule)

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single { UserRepository(get(), get(), get(), get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
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
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(),get()) }
    viewModel { PersonalInformationViewModel(get(), get()) }
    viewModel { LearnViewModel(get()) }
    viewModel { TutorialViewModel(get()) }
}
