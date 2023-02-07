package com.rockwallet.common.data.model

import android.os.Parcelable
import com.rockwallet.common.data.enums.AccessRestrictionReason
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = false)
data class KycAccessRights(
    @Json(name = "has_swap_access")
    val swapAccess: Boolean,

    @Json(name = "has_buy_access")
    val buyAccess: Boolean,

    @Json(name = "has_ach_access")
    val achAccess: Boolean,

    @Json(name = "restriction_reason")
    val restrictionReason: AccessRestrictionReason?
) : Parcelable