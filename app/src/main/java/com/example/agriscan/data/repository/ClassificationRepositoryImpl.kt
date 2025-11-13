package com.example.agriscan.data.repository

import android.graphics.Bitmap
import com.example.agriscan.domain.repository.ClassificationRepository
import com.example.agriscan.domain.util.DataError
import com.example.agriscan.domain.util.HttpResult
import com.example.agriscan.domain.util.Result
import com.example.agriscan.domain.util.httpResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.ByteArrayOutputStream

class ClassificationRepositoryImpl(
    private val httpClient: HttpClient
) : ClassificationRepository {
    override suspend fun classifyImage(bitmap: Bitmap): Result<String, DataError.Remote> {
        return when (val result = httpResult {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            httpClient.submitFormWithBinaryData(
                url = "https://gogurukul-sugarcane-breed-classifier.hf.space/predict",
                formData = formData {
                    append("file", byteArray, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                    })
                }
            ).body<String>()
        }) {
            is HttpResult.Success -> Result.Success(result.data)
            is HttpResult.Failure -> Result.Error(result.error)
        }
    }
}