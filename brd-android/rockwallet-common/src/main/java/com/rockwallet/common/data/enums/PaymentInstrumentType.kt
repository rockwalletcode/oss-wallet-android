package com.rockwallet.common.data.enums

import com.squareup.moshi.Json

enum class PaymentInstrumentType {
    @Json(name = "bank_account")
    BANK_ACCOUNT,

    @Json(name = "card")
    CREDIT_CARD
}