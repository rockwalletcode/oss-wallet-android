package com.rockwallet.kyc.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CountryState(
    @Json(name = "iso2")
    val code: String,

    @Json(name = "name")
    val name: String
): Parcelable