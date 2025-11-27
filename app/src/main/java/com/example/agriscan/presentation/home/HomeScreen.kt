package com.example.agriscan.presentation.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agriscan.R
import com.example.agriscan.presentation.AccentLime
import com.example.agriscan.presentation.BottomBarHeight
import com.example.agriscan.presentation.BrandGreen
import com.example.agriscan.presentation.BrandGreenDark
import com.example.agriscan.presentation.FabDiameter
import com.example.agriscan.presentation.LightStroke
import com.example.agriscan.presentation.Muted
import com.example.agriscan.presentation.OffBlack
import com.example.agriscan.presentation.navigation.Screen
import com.example.agriscan.ui.theme.AgriScanTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar


// ----------------- Root -----------------
@Composable
fun HomeRoot(navController: NavController, vm: HomeViewModel = koinViewModel()) {
    val uiState by vm.uiState.collectAsState()
    LaunchedEffect(Unit) {
        vm.syncScans()
    }
    HomeScreen(
        state = uiState,
        onAction = vm::onAction,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    navController: NavController,
) {
    var selectedRoute by remember { mutableStateOf("home") }

    Scaffold(
        topBar = { TopBar { onAction(HomeAction.LanguageChangeTapped) } },
        containerColor = Color.White
    ) { padding ->

        Box(Modifier.fillMaxSize()) {
            // space for floating bar + gesture pill
            val navInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val barStackHeight = BottomBarHeight + FabDiameter / 2
            val contentBottomPad = barStackHeight + 16.dp + navInset

            // -------- Your ORIGINAL content (no filler) --------
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = contentBottomPad),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { Spacer(Modifier.height(12.dp)) }
                item { IdentifyBanner() }
                item { Spacer(Modifier.height(16.dp)) }
                item {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)

                    val greeting = when (hour) {
                        in 0..11 -> R.string.good_morning
                        in 12..16 -> R.string.good_afternoon
                        else -> R.string.good_evening
                    }
                    WelcomeCard(name = state.userName, greeting = greeting)
                }
                item { Spacer(Modifier.height(20.dp)) }
                item { ScanAndIdentifyBanner() }
                item { Spacer(Modifier.height(20.dp)) }
                item { Dashboard(
                    numberOfScans = state.totalScans.toString(),
                    numberOfPredictions = state.totalScans.toString(),
                    uniqueBreeds = state.uniqueBreeds.toString(),
                    lastScanAddress = state.lastScanAddress
                ) }
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    LastPredictionRow(
                        date = state.lastScanDate,
                        breed = state.lastPredictedBreed,
                        onSupportClick = { onAction(HomeAction.SupportButtonTapped) }
                    )
                }
            }

            // -------- Floating CUSTOM bottom bar (pure Box, no BottomAppBar) --------
            CustomBottomBarFloating(
                selectedRoute = selectedRoute,
                onNavItemClick = {
                    onAction(HomeAction.NavItemTapped(it))
                    if (it == "learn") {
                        navController.navigate(Screen.LearnScreen)
                    } else if (it == "tutorial") {
                        navController.navigate(Screen.TutorialScreen)
                    } else if (it == "profile") {
                        navController.navigate(Screen.ProfileScreen)
                    } else {
                        selectedRoute = it
                    }
                },
                onScanClick = { onAction(HomeAction.ScanButtonTapped) 
                              navController.navigate(Screen.ScanScreen)
                },
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

// ----------------- UI Pieces -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onLanguageChange: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            Image(
                painter = painterResource(R.drawable.top_bar_app_logo),
                contentDescription = "Agri Scan Logo",
                modifier = Modifier.size(width = 200.dp, height = 40.dp)
            )
        },
        actions = {
            Text(
                text = "A/अ",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OffBlack,
                modifier = Modifier.clickable { onLanguageChange() }
            )
            Spacer(Modifier.width(16.dp))
            BadgedBox(badge = { Badge(containerColor = Color(0xFFE83B3B)) {} }) {
                Icon(Icons.Default.Notifications, "Notifications", tint = OffBlack)
            }
            Spacer(Modifier.width(8.dp))
        }
    )
}

@Composable
private fun IdentifyBanner() {
    Text(
        text = stringResource(id = R.string.identify_your_sugarcane),
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandGreen, RoundedCornerShape(4.dp))
            .padding(vertical = 8.dp),
        color = Color.White,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}

@Composable
private fun WelcomeCard(name: String, @StringRes greeting: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandGreen)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(id = R.string.hey_user, name), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(id = greeting), color = Color(0xFFE3F2E8), fontSize = 12.sp)
                Text(
                    stringResource(id = R.string.lets_identify_your_breed),
                    color = Color(0xFFE3F2E8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Image(
                painter = painterResource(R.drawable.upload_photo_banner),
                contentDescription = "Upload Photo",
                modifier = Modifier.size(width = 180.dp, height = 90.dp)
            )
        }
    }
}

@Composable
private fun ScanAndIdentifyBanner() {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Box {
            Image(
                painter = painterResource(R.drawable.sugarcane_home),
                contentDescription = "Scan Banner",
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = stringResource(id = R.string.scan_and_identify_your_breed),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(vertical = 8.dp),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun Dashboard(
    numberOfScans: String,
    numberOfPredictions: String,
    uniqueBreeds: String,
    lastScanAddress: String
) {
    val items = listOf(
        DashboardItem(
            labelRes = R.string.no_of_scans,
            value = numberOfScans,
            icon = R.drawable.ic_scan
        ),
        DashboardItem(
            labelRes = R.string.no_of_predictions,
            value = numberOfPredictions,
            icon = R.drawable.ic_prediction
        ),
        DashboardItem(
            labelRes = R.string.unique_breeds,
            value = uniqueBreeds,
            icon = R.drawable.ic_unique_breeds
        ),
        DashboardItem(
            labelRes = R.string.breed_location,
            value = lastScanAddress,
            icon = R.drawable.ic_location
        )
    )

    Column(horizontalAlignment = Alignment.Start) {
        Text(stringResource(id = R.string.dashboard), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OffBlack)
        Spacer(Modifier.height(14.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.height(196.dp),
            userScrollEnabled = false
        ) { items(items) { DashboardCard(it) } }
    }
}

@Composable
private fun DashboardCard(item: DashboardItem) {
    val label = stringResource(id = item.labelRes)
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BrandGreen),
        modifier = Modifier.height(90.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = label,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, maxLines = 1)
                Text(
                    item.value,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = if (item.labelRes == R.string.breed_location) 14.sp else 26.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LastPredictionRow(date: String, breed: String, onSupportClick: () -> Unit) {
    val formattedText = formatBreedText(breed)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.6.dp, LightStroke),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(BrandGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sugarcane_pred),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(65.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(stringResource(id = R.string.last_predicted_breed), fontSize = 12.sp, color = Muted)
                    Text(date, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandGreen)
                    Text(text = formattedText, fontSize = 14.sp, lineHeight = 18.sp, color = OffBlack)
                }
            }
        }

        IconButton(
            onClick = onSupportClick,
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(BrandGreen)
        ) {
            Icon(Icons.Default.Headset, stringResource(id = R.string.support), modifier = Modifier.size(40.dp), tint = Color.White)
        }
    }
}

private fun formatBreedText(rawText: String): AnnotatedString {
    // Example rawText: "✅ Sugarcane Detected (confidence: 93.03%) 🧠 Predicted Breed: Colk-16466 📊 Confidence: 82.09%"
    // We want to bold the titles like "Sugarcane Detected", "Predicted Breed:", "Confidence:"
    
    return buildAnnotatedString {
        val parts = rawText.split(Regex("(?=✅)|(?=🧠)|(?=📊)"))
        
        for (part in parts) {
            if (part.isBlank()) continue
            
            val trimmedPart = part.trim()
            val colonIndex = trimmedPart.indexOf(':')
            
            if (colonIndex != -1 && (trimmedPart.startsWith("🧠") || trimmedPart.startsWith("📊"))) {
                // For "🧠 Predicted Breed: Value" or "📊 Confidence: Value"
                // Bold the label part
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(trimmedPart.substring(0, colonIndex + 1))
                }
                append(trimmedPart.substring(colonIndex + 1))
            } else if (trimmedPart.startsWith("✅") || trimmedPart.startsWith("❌")) {
                 // For the main status line, make it all bold or just the start
                 // "✅ Sugarcane Detected (confidence: 93.03%)"
                 val openParenIndex = trimmedPart.indexOf('(')
                 if (openParenIndex != -1) {
                     withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(trimmedPart.substring(0, openParenIndex))
                     }
                     append(trimmedPart.substring(openParenIndex))
                 } else {
                     withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(trimmedPart)
                     }
                 }
            } else {
                append(trimmedPart)
            }
            append("\n")
        }
    }
}

/* ======================= CUSTOM FLOATING BOTTOM BAR ======================= */
/* Pure custom Box (no BottomAppBar). Uses your exact sizes/colors.          */

private val WhiteDiscDiameter = 48.dp
private val BumpRing = 3.dp

private val BumpCenterNudgeY = 5.dp
private val WhiteDiscNudgeY = 0.dp
private val FabNudgeY = 4.dp

private val NavIconSize = 70.dp
private val ScanIconSize = 60.dp
private val UnderBumpSideGap = 12.dp


@Composable
fun CustomBottomBarFloating(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier,
    scanLift: Dp = (-50).dp,
) {
    val shape = remember {
        CenterBumpBarShape(
            bumpRadius = WhiteDiscDiameter / 2 + BumpRing,
            barCorner = 24.dp,
            bumpCenterNudgeY = BumpCenterNudgeY
        )
    }
    val centerGap = maxOf(FabDiameter, WhiteDiscDiameter) + UnderBumpSideGap * 2

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BottomBarHeight + FabDiameter / 2),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Green bar body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .shadow(10.dp, shape)
                .clip(shape)
                .height(BottomBarHeight)
                .background(BrandGreenDark)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT two items (explicit, no NavItem list)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavCell(
                        route = "home",
                        iconRes = R.drawable.ic_home,
                        label = stringResource(id = R.string.home),
                        isSelected = selectedRoute == "home",
                        onClick = { onNavItemClick("home") }
                    )
                    NavCell(
                        route = "learn",
                        iconRes = R.drawable.ic_guide,
                        label = stringResource(id = R.string.learn),
                        isSelected = selectedRoute == "learn",
                        onClick = { onNavItemClick(
                            "learn"
                        ) }
                    )
                }

                Spacer(Modifier.width(centerGap))

                // RIGHT two items
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavCell(
                        route = "tutorial",
                        iconRes = R.drawable.ic_tutorial,
                        label = stringResource(id = R.string.tutorial),
                        isSelected = selectedRoute == "tutorial",
                        onClick = { onNavItemClick("tutorial") }
                    )
                    NavCell(
                        route = "profile",
                        iconRes = R.drawable.ic_profile,
                        label = stringResource(id = R.string.profile),
                        isSelected = selectedRoute == "profile",
                        onClick = { onNavItemClick("profile") }
                    )
                }
            }
        }


//         Raised scan button (fixed, not clipped)
        IconButton(
            onClick = onScanClick,
            modifier = Modifier
                .size(FabDiameter)
                .align(Alignment.TopCenter)
                .offset(y = scanLift + BumpCenterNudgeY + FabNudgeY)
                .clip(CircleShape)
                .background(BrandGreenDark)
                .zIndex(2f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bottom_bar_scna),
                contentDescription = stringResource(id = R.string.scan),
                tint = Color.Unspecified,
                modifier = Modifier.size(ScanIconSize)
            )
        }

    }
}

@Composable
private fun RowScope.NavCell(
    route: String,
    @DrawableRes iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (isSelected) AccentLime else Color.White
    val yOff = if (isSelected) (-6).dp else 0.dp // only selected lifts

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = tint,
            modifier = Modifier
                .size(NavIconSize)
                .offset(y = yOff)
        )
        if (isSelected) {
            Text(
                text = label,
                color = tint,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}

/** Rounded bar UNION circle for convex bump (same as your shape) */
private class CenterBumpBarShape(
    private val bumpRadius: Dp,
    private val barCorner: Dp,
    private val bumpCenterNudgeY: Dp = 0.dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val rCorner = with(density) { barCorner.toPx() }
        val rBump = with(density) { bumpRadius.toPx() }
        val nudgeY = with(density) { bumpCenterNudgeY.toPx() }

        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = -rBump + nudgeY  // circle center above bar top

        val base = Path().apply {
            addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(rCorner, rCorner)))
        }
        val bump = Path().apply {
            addOval(Rect(cx - rBump, cy - rBump, cx + rBump, cy + rBump))
        }
        val union = Path.combine(PathOperation.Union, base, bump)
        return Outline.Generic(union)
    }
}

private data class DashboardItem(
    @StringRes val labelRes: Int,
    val value: String,
    @DrawableRes val icon: Int
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852, backgroundColor = 0xFFFFFFFF)
@Composable
private fun HomePreview() {
    AgriScanTheme {
        HomeScreen(
            state = HomeState(),
            onAction = {},
            navController = rememberNavController()
        )
    }
}
