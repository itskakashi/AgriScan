package com.example.agriscan.data.repository

import com.example.agriscan.data.local.Location
import com.example.agriscan.data.remote.dto.NominatimReverseResponse
import com.example.agriscan.domain.repository.GeocodingRepository
import com.example.agriscan.domain.util.DataError
import com.example.agriscan.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GeocodingRepositoryImpl(
    private val httpClient: HttpClient
) : GeocodingRepository {
    override suspend fun getAddressFromCoordinates(location: Location): Result<String, DataError.Remote> {
        if (location.latitude == 0.0 && location.longitude == 0.0) {
            return Result.Error(DataError.Remote.UNKNOWN)
        }
        return try {
            val response = httpClient.get("https://nominatim.openstreetmap.org/reverse") {
                url {
                    parameters.append("lat", location.latitude.toString())
                    parameters.append("lon", location.longitude.toString())
                    parameters.append("format", "json")
                }
            }.body<NominatimReverseResponse>()
            val address = response.address
            if (address != null) {
                val addressParts = listOfNotNull(
                    address.city,
                    address.state,
                )
                Result.Success(addressParts.joinToString(", "))
            } else {
                Result.Error(DataError.Remote.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.NO_INTERNET)
        }
    }
}
