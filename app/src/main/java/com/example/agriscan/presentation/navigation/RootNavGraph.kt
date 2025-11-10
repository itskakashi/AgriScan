package com.example.agriscan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agriscan.presentation.auth.create_account.SignUpRoot
import com.example.agriscan.presentation.auth.forgot_password.ForgotPasswordRoot
import com.example.agriscan.presentation.auth.login.LoginRoot
import com.example.agriscan.presentation.auth.splash_screen.SplashScreen
import com.example.agriscan.presentation.home.HomeRoot
import com.example.agriscan.presentation.learn.LearnRoot
import com.example.agriscan.presentation.profile.PersonalInformationScreen
import com.example.agriscan.presentation.profile.ProfileRoot
import com.example.agriscan.presentation.result.ResultRoot
import com.example.agriscan.presentation.scan.ScanRoot
import com.example.agriscan.presentation.tutorial.TutorialRoot
import com.google.firebase.auth.FirebaseAuth
import org.koin.compose.koinInject

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()
    val firebaseAuth: FirebaseAuth = koinInject()
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen
    ) {
        composable<Screen.SplashScreen> {
            SplashScreen(
                onLogin = {
                    val destination = if (firebaseAuth.currentUser != null) Screen.HomeScreen else Screen.LoginScreen
                    navController.navigate(destination) {
                        popUpTo<Screen.SplashScreen> { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.LoginScreen> {
            LoginRoot(
                onLoginSuccess = {
                    navController.navigate(Screen.HomeScreen) {
                        popUpTo<Screen.LoginScreen> { inclusive = true }
                    }
                },
                onGoToSignUp = { navController.navigate(Screen.SignUpScreen) },
                onGoToForgotPassword = { navController.navigate(Screen.ForgotPasswordScreen) }
            )
        }
        composable<Screen.SignUpScreen> {
            SignUpRoot(
                onSignUpSuccess = {
                    navController.navigate(Screen.LoginScreen) {
                        popUpTo<Screen.SignUpScreen> { inclusive = true }
                    }
                },
                onGoToSignIn = {
                    navController.navigate(Screen.LoginScreen) {
                        popUpTo<Screen.SignUpScreen> { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.ForgotPasswordScreen> {
            ForgotPasswordRoot(
                onBackToLogin = { navController.navigateUp() },
                onGoToSignUp = { navController.navigate(Screen.SignUpScreen) }
            )
        }
        composable<Screen.HomeScreen> {
            HomeRoot(navController = navController)
        }
        composable<Screen.LearnScreen> {
            LearnRoot(navController = navController)
        }
        composable<Screen.TutorialScreen> {
            TutorialRoot(navController = navController)
        }
        composable<Screen.ProfileScreen> {
            ProfileRoot(navController = navController)
        }
        composable<Screen.PersonalInformationScreen> {
            PersonalInformationScreen(navController = navController)
        }
        composable<Screen.ScanScreen> {
            ScanRoot(navController = navController)
        }
        composable<Screen.ResultScreen> {
            ResultRoot(navController = navController)
        }
    }
}
