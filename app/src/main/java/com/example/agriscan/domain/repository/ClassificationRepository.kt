package com.example.agriscan.domain.repository

import android.graphics.Bitmap
import com.example.agriscan.domain.util.DataError
import com.example.agriscan.domain.util.Result

interface ClassificationRepository {
    suspend fun classifyImage(bitmap: Bitmap): Result<String, DataError.Remote>
}