package com.example.agriscan.data.repository

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.agriscan.domain.repository.ClassificationRepository
import com.example.agriscan.domain.util.DataError
import com.example.agriscan.domain.util.HttpResult
import com.example.agriscan.domain.util.Result
import com.example.agriscan.domain.util.httpResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayOutputStream

class ClassificationRepositoryImpl(
    private val httpClient: HttpClient
) : ClassificationRepository {
    override suspend fun classifyImage(bitmap: Bitmap): Result<String, DataError.Remote> {
        val result = httpResult {
            try {
                // 1. Resize and compress image
                // Smaller size to ensure base64 fallback works if upload fails
                val resizedBitmap = scaleBitmapDown(bitmap, 512)
                val stream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)
                val byteArray = stream.toByteArray()
                val size = byteArray.size

                var uploadedPath: String? = null

                // 2. Try Uploading image to Gradio server
                try {
                    Log.d("ClassificationRepo", "Attempting upload to /gradio_api/upload...")
                    val uploadResponse = httpClient.submitFormWithBinaryData(
                        url = "https://rajatgarg001-sugarcane-breed-classifer.hf.space/gradio_api/upload",
                        formData = formData {
                            append("files", byteArray, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                            })
                        }
                    ).body<String>()
                    
                    Log.d("ClassificationRepo", "Upload response: $uploadResponse")

                    val jsonElement = Json.parseToJsonElement(uploadResponse)
                    uploadedPath = if (jsonElement is kotlinx.serialization.json.JsonArray) {
                        jsonElement.jsonArray.firstOrNull()?.jsonPrimitive?.content
                    } else {
                         null
                    }
                } catch (e: Exception) {
                    Log.w("ClassificationRepo", "Upload failed, falling back to base64: ${e.message}")
                }

                // 3. Construct JSON Body
                val jsonBody = if (uploadedPath != null) {
                    Log.d("ClassificationRepo", "Using uploaded file path: $uploadedPath")
                    "{\"data\":[{\"path\":\"$uploadedPath\",\"orig_name\":\"image.jpg\",\"size\":$size,\"mime_type\":\"image/jpeg\",\"meta\":{\"_type\":\"gradio.FileData\"}}]}"
                } else {
                    Log.d("ClassificationRepo", "Using Base64 Data URI fallback")
                    val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                    val dataUri = "data:image/jpeg;base64,$base64String"
                    "{\"data\":[{\"path\":\"$dataUri\",\"orig_name\":\"image.jpg\",\"size\":$size,\"mime_type\":\"image/jpeg\",\"meta\":{\"_type\":\"gradio.FileData\"}}]}"
                }

                // 4. POST to initiate prediction
                val postResponse = httpClient.post("https://rajatgarg001-sugarcane-breed-classifer.hf.space/gradio_api/call/predict") {
                    contentType(ContentType.Application.Json)
                    setBody(jsonBody)
                }.body<String>()

                Log.d("ClassificationRepo", "POST response: $postResponse")

                val eventId = try {
                    val jsonElement = Json.parseToJsonElement(postResponse)
                    jsonElement.jsonObject["event_id"]?.jsonPrimitive?.content
                } catch (e: Exception) {
                    null
                }

                if (eventId.isNullOrBlank()) {
                    throw Exception("Failed to get event_id. Response: $postResponse")
                }

                // 5. GET result from stream
                val streamUrl = "https://rajatgarg001-sugarcane-breed-classifer.hf.space/gradio_api/call/predict/$eventId"
                val streamResponse = httpClient.get(streamUrl).body<String>()
                
                Log.d("ClassificationRepo", "Stream response: $streamResponse")

                val lines = streamResponse.lines()
                var dataContent: String? = null
                var isError = false
                
                for (line in lines) {
                    if (line.startsWith("event: error")) {
                        isError = true
                    }
                    if (line.startsWith("data:")) {
                        val content = line.substringAfter("data:").trim()
                        if (content != "null" && content.startsWith("[")) {
                             dataContent = content
                        }
                    }
                }

                if (isError) {
                     throw Exception("Gradio API returned error event during prediction.")
                }

                if (dataContent != null) {
                    "{\"data\": $dataContent}"
                } else {
                    throw Exception("No valid data found in stream response")
                }

            } catch (e: Exception) {
                Log.e("ClassificationRepo", "Error in classifyImage: ${e.message}", e)
                throw e
            }
        }

        return when (result) {
            is HttpResult.Success -> Result.Success(result.data)
            is HttpResult.Failure -> Result.Error(result.error)
        }
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = originalWidth
        var resizedHeight = originalHeight

        if (originalHeight > maxDimension || originalWidth > maxDimension) {
            if (originalWidth > originalHeight) {
                resizedWidth = maxDimension
                resizedHeight = (originalHeight * (maxDimension.toFloat() / originalWidth)).toInt()
            } else {
                resizedHeight = maxDimension
                resizedWidth = (originalWidth * (maxDimension.toFloat() / originalHeight)).toInt()
            }
            return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, true)
        }
        return bitmap
    }
}
