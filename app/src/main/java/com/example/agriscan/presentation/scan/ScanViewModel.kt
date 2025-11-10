package com.example.agriscan.presentation.scan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {

    private val _state = MutableStateFlow(ScanState())
    val state = _state.asStateFlow()

    private val _navigateToResult = MutableSharedFlow<Unit>()
    val navigateToResult = _navigateToResult.asSharedFlow()

    fun onAction(action: ScanAction, context: Context, cameraController: LifecycleCameraController) {
        when (action) {
            is ScanAction.OnCaptureClick -> {
                capturePhoto(context, cameraController)
            }

            is ScanAction.OnConfirmClick -> {
                onConfirmCapture()
            }

            is ScanAction.OnCancelClick -> {
                onCancelCapture()
            }
            else -> {}
        }
    }

    fun onImageSelected(uri: Uri, context: Context) {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        BitmapData.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        viewModelScope.launch {
            _navigateToResult.emit(Unit)
        }
    }

    private fun capturePhoto(context: Context, cameraController: LifecycleCameraController) {
        cameraController.takePicture(
            context.mainExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    _state.update {
                        it.copy(capturedImage = image.toBitmap())
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    // TODO: Handle error
                }
            }
        )
    }

    private fun onConfirmCapture() {
        state.value.capturedImage?.let { 
            BitmapData.bitmap = it
            viewModelScope.launch {
                _navigateToResult.emit(Unit)
            }
        }
    }

    private fun onCancelCapture() {
        _state.update {
            it.copy(capturedImage = null)
        }
    }

    fun getLastPhoto(context: Context) {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                _state.update {
                    it.copy(lastPhoto = uri)
                }
            }
        }
    }
}
