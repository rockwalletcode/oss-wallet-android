package com.rockwallet.trade.data.enums

import com.squareup.moshi.Json

enum class QuoteType {
    @Json(name = "SWAP")
    SWAP,

    @Json(name = "BUY_ACH")
    BUY_ACH,

    @Json(name = "BUY_CARD")
    BUY_CARD
}