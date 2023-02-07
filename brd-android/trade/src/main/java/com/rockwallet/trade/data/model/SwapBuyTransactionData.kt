package com.rockwallet.trade.data.model

import android.os.Parcelable
import com.rockwallet.trade.data.response.ExchangeOrderStatus
import com.rockwallet.trade.data.response.ExchangeType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
@JsonClass(generateAdapter = true)
data class SwapBuyTransactionData(

    @Json(name = "order_id")
    val exchangeId: String,

    @Json(name = "status")
    val exchangeStatus: ExchangeOrderStatus,

    @Json(name = "status_details")
    val exchangeStatusDetails: String,

    @Json(name = "source")
    val source: Data,

    @Json(name = "destination")
    val destination: Data,

    @Json(name = "timestamp")
    val timestamp: Long,

    @Json(name = "type")
    val type: ExchangeType

): Parcelable {

    fun isBuyTransaction() = type == ExchangeType.BUY || type == ExchangeType.BUY_ACH

    fun isSwapTransaction() = type == ExchangeType.SWAP

    fun getDepositCurrencyUpperCase() = source.currency.uppercase()

    fun getWithdrawalCurrencyUpperCase() = destination.currency.uppercase()
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Data(

    @Json(name = "currency")
    val currency: String,

    @Json(name = "currency_amount")
    val currencyAmount: BigDecimal,

    @Json(name = "transaction_id")
    val transactionId: String?
): Parcelable