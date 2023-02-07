package com.rockwallet.buy.data.response

import com.rockwallet.buy.data.enums.PaymentStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateBuyOrderResponse(
    @Json(name = "payment_reference")
    val paymentReference: String,

    @Json(name = "redirect_url")
    val redirectUrl: String?,

    @Json(name = "status")
    val status: PaymentStatus
)