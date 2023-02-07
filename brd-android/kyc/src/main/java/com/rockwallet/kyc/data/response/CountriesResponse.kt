package com.rockwallet.kyc.data.response

import com.rockwallet.kyc.data.model.Country
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountriesResponse(
    @Json(name = "countries")
    val countries: List<Country>
)