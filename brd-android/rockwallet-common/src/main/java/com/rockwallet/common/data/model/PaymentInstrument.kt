package com.rockwallet.common.data.model

import android.os.Parcelable
import com.rockwallet.common.R
import com.rockwallet.common.data.enums.PaymentInstrumentType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

sealed class PaymentInstrument(
    @Json(name = "type")
    val type: PaymentInstrumentType
): Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Card(
        @Json(name = "id")
        val id: String,

        @Json(name = "fingerprint")
        val fingerprint: String,

        @Json(name = "expiry_month")
        val expiryMonth: Int,

        @Json(name = "expiry_year")
        val expiryYear: Int,

        @Json(name = "scheme")
        val scheme: String,

        @Json(name = "last4")
        val last4Numbers: String
    ): PaymentInstrument(
        PaymentInstrumentType.CREDIT_CARD
    ) {
        val cardTypeIcon: Int
            get() = when {
                scheme.equals("visa", true) -> R.drawable.ic_visa
                scheme.equals("mastercard", true) -> R.drawable.ic_mastercard
                else -> R.drawable.ic_credit_card
            }

        val hiddenCardNumber: String
            get() = "**** **** **** $last4Numbers"

        val expiryDate: String
            get() = "$expiryMonth/${expiryYear % 100}"
    }

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class BankAccount(
        @Json(name = "id")
        val id: String,

        @Json(name = "account_name")
        val accountName: String?,

        @Json(name = "last4")
        val last4Numbers: String
    ): PaymentInstrument(
        PaymentInstrumentType.BANK_ACCOUNT
    )
}