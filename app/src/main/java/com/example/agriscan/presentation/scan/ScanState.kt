package com.example.agriscan.presentation.scan

import android.graphics.Bitmap
import android.net.Uri

data class ScanState(
    val isCameraReady: Boolean = false,
    val isImageCapturing: Boolean = false,
    val capturedImage: Bitmap? = null,
    val lastPhoto: Uri? = null
)
