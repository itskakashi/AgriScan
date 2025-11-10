package com.example.agriscan.presentation.scan

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.example.agriscan.R

@Composable
fun CameraPreview(
    onCaptureClick: () -> Unit,
    onGalleryClick: () -> Unit,
    cameraController: LifecycleCameraController,
    lastPhoto: Uri?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller = cameraController
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 64.dp)
        ) {
            if (lastPhoto != null) {
                Image(
                    painter = rememberAsyncImagePainter(lastPhoto),
                    contentDescription = "Last taken photo",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { onGalleryClick() },
                    contentScale = ContentScale.Crop
                )
            } else {
                IconButton(
                    onClick = onGalleryClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery),
                        contentDescription = "Open Gallery",
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
                    .clickable { onCaptureClick() }
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Capture Image",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun PermissionDenied(
    text: String,
    onRequestPermission: () -> Unit,
    onOpenSettings: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
        if (onOpenSettings != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onOpenSettings) {
                Text("Open Settings")
            }
        }
    }
}


@Composable
fun CaptureConfirmation(
    bitmap: Bitmap,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Captured Image",
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = onConfirm) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "OK",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
