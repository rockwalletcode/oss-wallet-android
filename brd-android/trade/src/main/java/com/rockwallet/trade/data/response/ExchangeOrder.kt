package com.rockwallet.trade.data.response

import com.rockwallet.common.data.model.PaymentInstrument
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class ExchangeOrder(
    @Json(name = "order_id")
    val orderId: String,

    @Json(name = "status")
    val status: ExchangeOrderStatus,

    @Json(name = "source")
    val source: ExchangeSource,

    @Json(name = "destination")
    val destination: ExchangeSource,

    @Json(name = "timestamp")
    val timestamp: Long,

    @Json(name="rate")
    val rate: BigDecimal?,

    @Json(name="type")
    val type: ExchangeType?
)

@JsonClass(generateAdapter = true)
data class ExchangeSource(
    @Json(name = "currency")
    val currency: String,

    @Json(name = "usd_amount")
    val usdAmount: BigDecimal?,

    @Json(name = "currency_amount")
    val currencyAmount: BigDecimal,

    @Json(name = "transaction_id")
    val transactionId: String?,

    @Json(name = "payment_instrument")
    val paymentInstrument: PaymentInstrument?,

    @Json(name = "usd_fee")
    val usdFee: BigDecimal?,

    @Json(name = "fee_rate")
    val feeRate: Float?,

    @Json(name = "fee_fixed_rate")
    val feeFixedRate: BigDecimal?,
)

enum class ExchangeOrderStatus {
    @Json(name = "PENDING")
    PENDING,

    @Json(name = "FAILED")
    FAILED,

    @Json(name = "COMPLETE")
    COMPLETE,

    @Json(name = "REFUNDED")
    REFUNDED,

    @Json(name = "MANUALLY_SETTLED")
    MANUALLY_SETTLED,
}

enum class ExchangeType {
    @Json(name = "BUY")
    BUY,

    @Json(name = "BUY_ACH")
    BUY_ACH,

    @Json(name = "SWAP")
    SWAP
}