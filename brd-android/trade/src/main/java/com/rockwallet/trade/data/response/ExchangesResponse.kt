package com.rockwallet.trade.data.response

import com.rockwallet.trade.data.model.SwapBuyTransactionData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangesResponse(
    @Json(name = "exchanges")
    val exchanges: List<SwapBuyTransactionData>
)