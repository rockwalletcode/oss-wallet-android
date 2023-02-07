package com.rockwallet.buy.data.response

import com.rockwallet.buy.data.enums.PaymentStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentStatusResponse(
    @Json(name = "payment_reference")
    val paymentReference: String,

    @Json(name = "status")
    val status: PaymentStatus,

    @Json(name = "response_code")
    val responseCode: String?
)