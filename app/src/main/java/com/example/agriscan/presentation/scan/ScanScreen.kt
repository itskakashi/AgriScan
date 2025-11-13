package com.example.agriscan.presentation.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agriscan.R
import com.example.agriscan.domain.util.ObserveAsEvents
import com.example.agriscan.presentation.navigation.Screen
import com.example.agriscan.ui.theme.AgriScanTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanRoot(
    viewModel: ScanViewModel = koinViewModel(),
    navController: NavController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigateToResult.collectLatest { screen ->
            navController.navigate(screen)
        }
    }

    ObserveAsEvents(flow = viewModel.navigateToHome) {
        snackbarHostState.showSnackbar("Failed to classify image")
        navController.navigate(Screen.HomeScreen) {
            popUpTo(Screen.HomeScreen) { inclusive = true }
        }
    }

    ScanScreen(
        state = state,
        onAction = viewModel::onAction,
        onImageSelected = viewModel::onImageSelected,
        getLastPhoto = { viewModel.getLastPhoto(context) },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ScanScreen(
    state: ScanState,
    onAction: (ScanAction, Context, LifecycleCameraController) -> Unit,
    onImageSelected: (Uri, Context) -> Unit,
    getLastPhoto: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val cameraController = remember { LifecycleCameraController(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasLocationPermission = granted
        }
    )
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
            if (granted) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    )

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasStoragePermission = granted
            if (granted) {
                getLastPhoto()
            }
        }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                onImageSelected(it, context)
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event.targetState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
                cameraController.bindToLifecycle(lifecycleOwner)
            } else {
                cameraController.unbind()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(hasStoragePermission) {
        if (hasStoragePermission) {
            getLastPhoto()
        } else {
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    LaunchedEffect(key1 = true) {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
        ) {
            if (hasCamPermission) {
                if (state.capturedImage != null) {
                    CaptureConfirmation(
                        bitmap = state.capturedImage,
                        onConfirm = { onAction(ScanAction.OnConfirmClick, context, cameraController) },
                        onCancel = { onAction(ScanAction.OnCancelClick, context, cameraController) }
                    )
                } else {
                    CameraPreview(
                        onCaptureClick = { onAction(ScanAction.OnCaptureClick, context, cameraController) },
                        onGalleryClick = { galleryLauncher.launch("image/*") },
                        cameraController = cameraController,
                        lastPhoto = state.lastPhoto
                    )
                }
            } else {
                PermissionDenied(
                    text = stringResource(id = R.string.camera_permission_required),
                    onRequestPermission = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                    onOpenSettings = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            }
            if (state.isClassifying) {
                LoadingScreen()
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun Preview() {
    AgriScanTheme {
        ScanScreen(
            state = ScanState(),
            onAction = { _, _, _ -> },
            onImageSelected = { _, _ -> },
            getLastPhoto = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
