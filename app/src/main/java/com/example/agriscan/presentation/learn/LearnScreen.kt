package com.example.agriscan.presentation.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agriscan.R
import com.example.agriscan.presentation.AccentLime
import com.example.agriscan.presentation.BottomBarHeight
import com.example.agriscan.presentation.BrandGreenDark
import com.example.agriscan.presentation.FabDiameter
import com.example.agriscan.presentation.home.CustomBottomBarFloating
import com.example.agriscan.presentation.home.TopBar
import com.example.agriscan.presentation.navigation.Screen
import com.example.agriscan.ui.theme.AgriScanTheme
import org.koin.androidx.compose.koinViewModel


// ----------------- Root -----------------
@Composable
fun LearnRoot(
    viewModel: LearnViewModel = koinViewModel(),
    navController: NavController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LearnScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController,
        onNotificationClick = { navController.navigate(Screen.NotificationScreen) }
    )
}

// ----------------- Screen -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    state: LearnState,
    onAction: (LearnAction) -> Unit,
    navController: NavController,
    onNotificationClick: () -> Unit
) {
    var selectedRoute by remember { mutableStateOf("learn") }

    Scaffold(
        topBar = { TopBar(onLanguageChange = { onAction(LearnAction.LanguageChangeTapped) }, onNotificationClick = onNotificationClick) }, // Reusable Top Bar
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
                item { IdentifyBanner() }
                item { LearnAboutCard() }
                items(state.breeds) { breed ->
                    BreedInfoCard(breed = breed)
                }
            }

            // Reusable Floating Bottom Bar
            CustomBottomBarFloating(
                selectedRoute = selectedRoute,
                onNavItemClick = { route ->
                    if (route == "home") {
                        navController.navigate(Screen.HomeScreen)
                    } else if (route == "tutorial") {
                        navController.navigate(Screen.TutorialScreen)
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
                scanLift = (1).dp // adjust if you want it higher/lower
            )
        }
    }
}

// ----------------- UI Pieces for Learn Screen -----------------

@Composable
private fun IdentifyBanner() {
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

@Composable
private fun LearnAboutCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(id = R.string.learn_about_sugarcane_breeds),
                color = BrandGreenDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(id = R.string.explore_sugarcane_varieties),
                color = BrandGreenDark.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun BreedInfoCard(breed: SugarcaneBreed) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandGreenDark),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(id = breed.name), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            BreedDetailRow(stringResource(id = R.string.type), stringResource(id = breed.type))
            BreedDetailRow(stringResource(id = R.string.region), stringResource(id = breed.region))
            BreedDetailRow(stringResource(id = R.string.maturity), stringResource(id = breed.maturity))
            BreedDetailRow(stringResource(id = R.string.juice_yield), stringResource(id = breed.juiceYield))
            BreedDetailRow(stringResource(id = R.string.description), stringResource(id = breed.description))
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                breed.images.forEach { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(id = R.string.key_characteristics),
                        color = AccentLime,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringArrayResource(id = breed.keyCharacteristics).joinToString("\n"),
                        color = Color.White, fontSize = 12.sp, lineHeight = 18.sp
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.growing_tips), color = AccentLime, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = stringArrayResource(id = breed.growingTips).joinToString("\n"),
                        color = Color.White, fontSize = 12.sp, lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BreedDetailRow(label: String, value: String) {
    Text(
        modifier = Modifier.padding(bottom = 2.dp),
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = AccentLime, fontWeight = FontWeight.Bold)) {
                append(label)
                append(" ")
            }
            append(value)
        },
        color = Color.White,
        fontSize = 14.sp,
    )
}

// ----------------- Preview -----------------

@Preview(showBackground = true, backgroundColor = 0xFFF0F2EF)
@Composable
private fun Preview() {
    AgriScanTheme {
        // Use the ViewModel in the preview to get realistic data
        LearnRoot(navController = rememberNavController())
    }
}
