package com.rockwallet.buy.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CreateBuyAchOrderRequest(
    @Json(name = "quote_id")
    val quoteId: String,

    @Json(name = "deposit_quantity")
    val depositQuantity: BigDecimal,

    @Json(name = "withdrawal_quantity")
    val withdrawQuantity: BigDecimal,

    @Json(name = "destination")
    val destination: String,

    @Json(name = "account_id")
    val accountId: String
)
