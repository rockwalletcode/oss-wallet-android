package com.rockwallet.common.data.enums

import com.squareup.moshi.Json

enum class AccessRestrictionReason {

    @Json(name = "kyc")
    KYC,

    @Json(name = "country")
    COUNTRY,

    @Json(name = "state")
    STATE
}