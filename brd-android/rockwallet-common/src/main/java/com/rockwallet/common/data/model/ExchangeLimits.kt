package com.rockwallet.common.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
@JsonClass(generateAdapter = false)
data class ExchangeLimits(
    @Json(name = "swap_allowance_lifetime")
    val swapAllowanceLifetime: BigDecimal?,

    @Json(name = "swap_allowance_daily")
    val swapAllowanceDaily: BigDecimal?,

    @Json(name = "swap_allowance_per_exchange")
    val swapAllowancePerExchange: BigDecimal?,

    @Json(name = "used_swap_lifetime")
    val swapUsedLifetime: BigDecimal?,

    @Json(name = "used_swap_daily")
    val swapUsedDaily: BigDecimal?,

    @Json(name = "buy_allowance_lifetime")
    val buyAllowanceLifetime: BigDecimal?,

    @Json(name = "buy_allowance_daily")
    val buyAllowanceDaily: BigDecimal?,

    @Json(name = "buy_allowance_per_purchase")
    val buyAllowancePerExchange: BigDecimal?,

    @Json(name = "used_buy_lifetime")
    val buyUsedLifetime: BigDecimal?,

    @Json(name = "used_buy_daily")
    val buyUsedDaily: BigDecimal?,

    @Json(name = "buy_ach_allowance_lifetime")
    val buyAchAllowanceLifetime: BigDecimal?,

    @Json(name = "buy_ach_allowance_daily")
    val buyAchAllowanceDaily: BigDecimal?,

    @Json(name = "buy_ach_allowance_per_purchase")
    val buyAchAllowancePerExchange: BigDecimal?,

    @Json(name = "used_buy_ach_lifetime")
    val buyAchUsedLifetime: BigDecimal?,

    @Json(name = "used_buy_ach_daily")
    val buyAchUsedDaily: BigDecimal?
) : Parcelable {

    val availableBuyDailyLimit: BigDecimal?
        get() = buyAllowanceDaily?.let { allowanceDaily ->
            buyUsedDaily?.let { usedDaily ->
                allowanceDaily - usedDaily
            }
        }

    val availableBuyAchDailyLimit: BigDecimal?
        get() = buyAchAllowanceDaily?.let { allowanceDaily ->
            buyAchUsedDaily?.let { usedDaily ->
                allowanceDaily - usedDaily
            }
        }

    val availableBuyLifetimeLimit: BigDecimal?
        get() = buyAllowanceLifetime?.let { allowanceLifetime ->
            buyUsedLifetime?.let { usedLifetime ->
                allowanceLifetime - usedLifetime
            }
        }

    val availableBuyAchLifetimeLimit: BigDecimal?
        get() = buyAchAllowanceLifetime?.let { allowanceLifetime ->
            buyAchUsedLifetime?.let { usedLifetime ->
                allowanceLifetime - usedLifetime
            }
        }
}