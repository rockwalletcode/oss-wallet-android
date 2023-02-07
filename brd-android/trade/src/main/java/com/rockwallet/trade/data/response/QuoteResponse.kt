package com.rockwallet.trade.data.response

import android.os.Parcelable
import com.rockwallet.trade.data.enums.QuoteType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "quote_id")
    val quoteId: String,

    @Json(name = "exchange_rate")
    val exchangeRate: BigDecimal,

    @Json(name = "timestamp")
    val timestamp: Long,

    @Json(name="minimum_value")
    val minimumValue: BigDecimal,

    @Json(name="buy_fees")
    val buyCardFeesPercent: Float?,

    @Json(name="ach_fees")
    val buyAchFees: AchFees?,

    @Json(name = "maximum_value")
    val maximumValue: BigDecimal,

    @Json(name = "minimum_value_usd")
    val minimumValueUsd: BigDecimal?,

    @Json(name = "maximum_value_usd")
    val maximumValueUsd: BigDecimal?,

    @Json(name="from_fee_currency")
    val fromFeeCurrency: FeeCurrency?,

    @Json(name="to_fee_currency")
    val toFeeCurrency: FeeCurrency?,

    @Json(name="from_fee")
    val fromFee: BigDecimal?,

    @Json(name="to_fee")
    val toFee: BigDecimal?,

    @Json(name="type")
    val type: QuoteType
): Parcelable {

    fun isExpired() = timestamp < System.currentTimeMillis()
}

@Parcelize
@JsonClass(generateAdapter = true)
data class FeeCurrency(
    @Json(name = "fee_currency")
    val currency: String,

    @Json(name = "rate")
    val rate: BigDecimal
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class AchFees(
    @Json(name = "ach_fee_fixed_usd")
    val feeFixedUsd: BigDecimal,

    @Json(name = "ach_fee_percentage")
    val feePercentage: Float
): Parcelable
