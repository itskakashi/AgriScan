package com.example.agriscan.presentation.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.agriscan.presentation.BottomBarHeight
import com.example.agriscan.presentation.BrandGreenDark
import com.example.agriscan.presentation.FabDiameter
import com.example.agriscan.presentation.home.CustomBottomBarFloating
import com.example.agriscan.presentation.home.TopBar
import com.example.agriscan.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import com.example.agriscan.R


@Composable
fun TutorialRoot(
    viewModel: TutorialViewModel = koinViewModel(),
    navController: NavController,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TutorialScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController,
        onNotificationClick = { navController.navigate(Screen.NotificationScreen) }
    )
}

@Composable
fun TutorialScreen(
    state: TutorialState,
    onAction: (TutorialAction) -> Unit,
    navController: NavController,
    onNotificationClick: () -> Unit
) {
    // keep track of selected bottom route (your project probably tracks this globally)
    var selectedRoute by remember { mutableStateOf("tutorial") }

    Scaffold(
        topBar = { TopBar(onLanguageChange = { onAction(TutorialAction.LanguageChangeTapped) }, onNotificationClick = onNotificationClick) },
        containerColor = Color(0xFFF0F2EF)
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            val navInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val barStackHeight = BottomBarHeight + FabDiameter / 2
            val contentBottomPad = barStackHeight + 16.dp + navInset

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = contentBottomPad),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.identify_your_sugarcane),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandGreenDark, RoundedCornerShape(4.dp))
                            .padding(vertical = 8.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                item {
                    Button(
                        onClick = {
                            // toggle play/pause
                            if (state.isPlaying) onAction(TutorialAction.PauseVideo)
                            else onAction(TutorialAction.PlayVideo)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = if (state.isPlaying) stringResource(id = R.string.pause_tutorial) else stringResource(id = R.string.play_the_tutorial_video),
                            color = BrandGreenDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    // height clamp / spacing - You can wrap in Card to get elevation if desired
                    YoutubePlayer(
                        youtubeVideoId = state.youtubeVideoId,
                        modifier = Modifier.fillMaxWidth(),
                        autoPlay = state.isPlaying
                    )
                }

                // spacer to prevent content behind bottom bar
                item { androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Floating bottom bar (your implementation)
            CustomBottomBarFloating(
                selectedRoute = selectedRoute,
                onNavItemClick = { route ->
                    if (route == "home") {
                        navController.navigate(Screen.HomeScreen)
                    } else if (route == "learn") {
                        navController.navigate(Screen.LearnScreen)
                    } else if (route == "profile") {
                        navController.navigate(Screen.ProfileScreen)
                    } else {
                        selectedRoute = route
                    }
                },
                onScanClick = { navController.navigate(Screen.ScanScreen) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 12.dp, end = 12.dp,
                        bottom = navInset + 6.dp
                    )
                    .zIndex(2f),
                scanLift = (1).dp
            )
        }
    }
}
