package com.rockwallet.buy.data.response

import com.rockwallet.common.data.model.PaymentInstrument
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentInstrumentsResponse(
    @Json(name = "payment_instruments")
    val paymentInstruments: List<PaymentInstrument>
)