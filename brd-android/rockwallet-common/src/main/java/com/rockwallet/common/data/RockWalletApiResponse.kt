package com.rockwallet.common.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RockWalletApiResponse<T>(
    @Json(name = "result")
    val result: String,

    @Json(name = "error")
    val error: RockWalletApiResponseError?,

    @Json(name = "data")
    val data: T?,
)

@JsonClass(generateAdapter = true)
data class RockWalletApiResponseError(
    @Json(name = "code")
    val code: String,

    @Json(name = "server_message")
    val message: String?
)