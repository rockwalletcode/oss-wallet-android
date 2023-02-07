package com.rockwallet.buy.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaidLinkResponse(
    @Json(name = "link_token")
    val linkToken: String
)