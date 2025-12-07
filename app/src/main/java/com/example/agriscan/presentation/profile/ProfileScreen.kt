package com.example.agriscan.presentation.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agriscan.R
import com.example.agriscan.presentation.BottomBarHeight
import com.example.agriscan.presentation.FabDiameter
import com.example.agriscan.presentation.home.CustomBottomBarFloating
import com.example.agriscan.presentation.navigation.Screen
import com.example.agriscan.ui.theme.AgriScanTheme
import com.example.agriscan.util.LanguageManager
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import org.koin.compose.koinInject

@Composable
fun ProfileRoot(
    viewModel: ProfileViewModel = koinViewModel(),
    navController: NavController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.refresh()
    }

    ProfileScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action, context) },
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    navController: NavController,
    languageManager: LanguageManager = koinInject()
) {
    var selectedRoute by remember { mutableStateOf("profile") }
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasStoragePermission = granted
        }
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { onAction(ProfileAction.OnProfileImageChanged(it)) }
        }
    )

    if (showLanguageDialog) {
        LanguageDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = {
                languageManager.setLanguage(it)
                showLanguageDialog = false
                activity.recreate()
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { currentPassword, newPassword ->
                onAction(ProfileAction.OnChangePassword(currentPassword, newPassword))
                showChangePasswordDialog = false
            }
        )
    }

    Scaffold(
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
                item { ProfileHeader(state = state, onEditClick = { if(hasStoragePermission) imagePicker.launch("image/*") else launcher.launch(Manifest.permission.READ_MEDIA_IMAGES) }) }
                item { ProfileStats(state = state) }
                item {
                    Settings(
                        navController = navController,
                        onLanguageClick = { showLanguageDialog = true },
                        onChangePasswordClick = { showChangePasswordDialog = true },
                        onLogoutClick = { onAction(ProfileAction.Logout) })
                }
            }

            CustomBottomBarFloating(
                selectedRoute = selectedRoute,
                onNavItemClick = { route ->
                    if (route == "home") {
                        navController.navigate(Screen.HomeScreen)
                    } else if (route == "learn") {
                        navController.navigate(Screen.LearnScreen)
                    } else if (route == "tutorial") {
                        navController.navigate(Screen.TutorialScreen)
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

@Composable
fun ProfileHeader(state: ProfileState, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { onEditClick() },
            contentAlignment = Alignment.Center
        ) {
            if (state.profileImage.isEmpty()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile Image",
                    modifier = Modifier.size(100.dp),
                    tint = Color.Gray
                )
            } else {
                AsyncImage(
                    model = state.profileImage,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = state.userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = state.userEmail, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
    }
}

@Composable
fun ProfileStats(state: ProfileState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(labelRes = R.string.no_of_scans, value = state.totalScans.toString())
            Divider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            )
            StatItem(labelRes = R.string.no_of_predictions, value = state.totalScans.toString())
            Divider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            )
            StatItem(labelRes = R.string.unique_breeds, value = state.uniqueBreeds.toString())
        }
    }
}

@Composable
fun StatItem(@StringRes labelRes: Int, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = labelRes),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun Settings(
    navController: NavController,
    onLanguageClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingsItem(
            icon = R.drawable.person,
            text = stringResource(id = R.string.personal_information),
            onClick = { navController.navigate(Screen.PersonalInformationScreen) },
            isProfile = true
        )
        SettingsItem(
            icon = R.drawable.ic_language,
            text = stringResource(id = R.string.language),
            onClick = onLanguageClick
        )
        SettingsItem(
            icon = R.drawable.ic_lock,
            text = stringResource(id = R.string.change_password),
            onClick = onChangePasswordClick
        )
        SettingsItem(
            icon = R.drawable.ic_logout,
            text = stringResource(id = R.string.logout),
            onClick = {
                onLogoutClick()
                navController.navigate(Screen.LoginScreen) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        )
    }
}

@Composable
fun SettingsItem(@DrawableRes icon: Int, text: String, onClick: () -> Unit, isProfile: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
    }
}

@Composable
fun LanguageDialog(onDismiss: () -> Unit, onLanguageSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language") },
        text = {
            Column {
                Text(
                    text = "English",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected("en") }
                        .padding(vertical = 12.dp)
                )
                Text(
                    text = "हिंदी (Hindi)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected("hi") }
                        .padding(vertical = 12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface
    )
}


@Preview
@Composable
fun Preview() {
    AgriScanTheme {
        ProfileScreen(
            state = ProfileState(),
            onAction = {},
            navController = rememberNavController()
        )
    }
}
