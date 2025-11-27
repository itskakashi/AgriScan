package com.example.agriscan.presentation.scan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agriscan.data.local.Location
import com.example.agriscan.data.local.entities.Scan
import com.example.agriscan.domain.repository.ClassificationRepository
import com.example.agriscan.domain.repository.GeocodingRepository
import com.example.agriscan.domain.repository.ScanRepository
import com.example.agriscan.domain.util.DataError
import com.example.agriscan.domain.util.Result
import com.example.agriscan.presentation.navigation.Screen
import com.example.agriscan.util.LocationTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

class ScanViewModel(
    private val classificationRepository: ClassificationRepository,
    private val scanRepository: ScanRepository,
    private val locationTracker: LocationTracker,
    private val geocodingRepository: GeocodingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScanState())
    val state = _state.asStateFlow()

    private val _navigateToResult = MutableSharedFlow<Screen.ResultScreen>()
    val navigateToResult = _navigateToResult.asSharedFlow()

    private val _navigateToHome = MutableSharedFlow<Unit>()
    val navigateToHome = _navigateToHome.asSharedFlow()

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
        classifyImage(bitmap.copy(Bitmap.Config.ARGB_8888, true))
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
                    Log.e("ScanViewModel", "Capture failed: ${exception.message}")
                }
            }
        )
    }

    private fun onConfirmCapture() {
        state.value.capturedImage?.let {
            classifyImage(it)
        }
    }

    private fun classifyImage(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isClassifying = true) }

            try {
                val classificationResultDeferred = async { classificationRepository.classifyImage(bitmap) }
                
                val locationForGeocoding = try {
                    val currentLocation = locationTracker.getCurrentLocation()
                    if (currentLocation != null) {
                        Location(latitude = currentLocation.latitude, longitude = currentLocation.longitude)
                    } else {
                        Location(0.0, 0.0)
                    }
                } catch (e: Exception) {
                    Log.e("ScanViewModel", "Location error: ${e.message}")
                    Location(0.0, 0.0)
                }

                val addressResultDeferred = async { 
                    try {
                        geocodingRepository.getAddressFromCoordinates(locationForGeocoding) 
                    } catch (e: Exception) {
                        Log.e("ScanViewModel", "Geocoding error: ${e.message}")
                        Result.Error(DataError.Remote.UNKNOWN)
                    }
                }

                val classificationResult = try {
                    classificationResultDeferred.await()
                } catch (e: Exception) {
                    Log.e("ScanViewModel", "Classification error: ${e.message}")
                    Result.Error(DataError.Remote.UNKNOWN)
                }
                
                val addressResult = addressResultDeferred.await()

                when(classificationResult) {
                    is Result.Success -> {
                        val address = if (addressResult is Result.Success) addressResult.data else ""
                        Log.d("ScanViewModel", "API Response: ${classificationResult.data}")
                        
                        try {
                            val jsonElement = Json.parseToJsonElement(classificationResult.data)
                            val jsonObject = jsonElement.jsonObject
                            
                            val breedName = if (jsonObject.containsKey("data")) {
                                val dataArray = jsonObject["data"]?.jsonArray
                                val firstItem = dataArray?.firstOrNull()
                                
                                if (firstItem != null) {
                                    try {
                                        // Try as String first
                                        firstItem.jsonPrimitive.content
                                    } catch (e: IllegalArgumentException) {
                                        // Try as Object
                                        val label = firstItem.jsonObject["label"]?.jsonPrimitive?.contentOrNull
                                        if (!label.isNullOrBlank()) {
                                            label
                                        } else {
                                            // Try confidences array in Object
                                            val confidences = firstItem.jsonObject["confidences"]?.jsonArray
                                            val firstConfidence = confidences?.firstOrNull()?.jsonObject
                                            firstConfidence?.get("label")?.jsonPrimitive?.contentOrNull ?: ""
                                        }
                                    }
                                } else {
                                    ""
                                }
                            } else {
                                 jsonObject["label"]?.jsonPrimitive?.contentOrNull 
                                 ?: jsonObject["prediction"]?.jsonPrimitive?.contentOrNull 
                                 ?: ""
                            }

                            // Accept result if it's not blank. The specific disease/healthy checks might be too restrictive
                            if (breedName.isNotBlank()) {
                                 val scan = Scan(
                                    breedName = breedName,
                                    latitude = locationForGeocoding.latitude,
                                    longitude = locationForGeocoding.longitude,
                                    timestamp = System.currentTimeMillis(),
                                    address = address
                                )
                                scanRepository.insertScan(scan)
                                _state.update { it.copy(classificationResult = breedName, isClassifying = false) }
                                BitmapData.bitmap = bitmap
                                _navigateToResult.emit(
                                    Screen.ResultScreen(
                                        result = breedName,
                                        address = address,
                                        latitude = locationForGeocoding.latitude,
                                        longitude = locationForGeocoding.longitude
                                    )
                                )
                            } else {
                                 Log.w("ScanViewModel", "Classification result rejected: $breedName. Raw response: ${classificationResult.data}")
                                 _state.update { it.copy(classificationResult = "Classification Failed", isClassifying = false) }
                                 _navigateToHome.emit(Unit)
                            }
                        } catch (e: Exception) {
                             Log.e("ScanViewModel", "JSON parsing error: ${e.message}. Raw response: ${classificationResult.data}")
                             _state.update { it.copy(classificationResult = "Error parsing result: ${e.message}", isClassifying = false) }
                             _navigateToHome.emit(Unit)
                        }
                    }
                    is Result.Error -> {
                        Log.e("ScanViewModel", "Classification API error: ${classificationResult.error}")
                        _state.update { it.copy(classificationResult = classificationResult.error.toString(), isClassifying = false) }
                        _navigateToHome.emit(Unit)
                    }
                }
            } catch (e: Exception) {
                Log.e("ScanViewModel", "Unexpected error: ${e.message}")
                _state.update { it.copy(classificationResult = "Error: ${e.message}", isClassifying = false) }
                _navigateToHome.emit(Unit)
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
