package com.example.agriscan.domain.repository

import com.example.agriscan.data.local.Location
import com.example.agriscan.domain.util.Result
import com.example.agriscan.domain.util.DataError

interface GeocodingRepository {
    suspend fun getAddressFromCoordinates(location: Location): Result<String, DataError.Remote>
}
