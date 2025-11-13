package com.example.agriscan.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NominatimReverseResponse(
    val address: Address? = null
) {
    @Serializable
    data class Address(
        val road: String? = null,
        val suburb: String? = null,
        val city: String? = null,
        val county: String? = null,
        val state: String? = null,
        val postcode: String? = null,
        val country: String? = null
    )
}
