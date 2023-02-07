package com.rockwallet.buy.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostPlaidTokenRequest(
    @Json(name = "public_token")
    val token: String,

    @Json(name = "mask")
    val mask: String?
)
