package com.rockwallet.buy.ui.features.orderpreview

import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.trade.data.model.FeeAmountData
import com.rockwallet.trade.data.response.ExchangeType
import com.rockwallet.trade.data.response.QuoteResponse
import java.math.BigDecimal
import java.math.RoundingMode

class OrderPreviewContract : RockWalletContract {

    sealed class Event : RockWalletContract.Event {
        object OnBackPressed : Event()
        object OnDismissClicked : Event()
        object OnConfirmClicked : Event()
        object OnCreditInfoClicked : Event()
        object OnNetworkInfoClicked : Event()
        object OnSecurityCodeInfoClicked : Event()
        object OnTermsAndConditionsClicked : Event()
        object OnUserAuthenticationSucceed : Event()

        data class OnTermsChanged(val accepted: Boolean) : Event()
        data class OnSecurityCodeChanged(val securityCode: String) : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object TimeoutScreen : Effect()
        object RequestUserAuthentication : Effect()

        data class ShowError(val message: String) : Effect()
        data class PaymentProcessing(
            val paymentType: ExchangeType,
            val paymentReference: String?,
            val redirectUrl: String?,
        ) : Effect()

        data class ShowInfoDialog(
            val image: Int? = null,
            val title: Int,
            val description: Int
        ) : Effect()

        data class OpenWebsite(val url: String): Effect()
    }

    data class State(
        val securityCode: String = "",
        val fiatCurrency: String,
        val cryptoCurrency: String,
        val fiatAmount: BigDecimal,
        val networkFee: FeeAmountData,
        val quoteResponse: QuoteResponse?,
        val paymentInstrument: PaymentInstrument,
        val termsAccepted: Boolean = false,
        val confirmButtonEnabled: Boolean = false,
        val fullScreenLoadingIndicator: Boolean = false
    ) : RockWalletContract.State {

        val oneFiatUnitToCryptoRate: BigDecimal
            get() = quoteResponse?.exchangeRate ?: BigDecimal.ZERO

        val oneCryptoUnitToFiatRate: BigDecimal
            get() = BigDecimal.ONE.divide(quoteResponse?.exchangeRate, 20, RoundingMode.HALF_UP) ?: BigDecimal.ZERO

        val totalFiatAmount: BigDecimal
            get() = fiatAmount + networkFee.fiatAmount + when (paymentInstrument) {
                is PaymentInstrument.BankAccount -> achFee
                is PaymentInstrument.Card -> cardFee
            }

        val cryptoAmount: BigDecimal
            get() = fiatAmount * oneFiatUnitToCryptoRate

        val cardFee: BigDecimal
            get() = fiatAmount * (cardFeePercent / 100).toBigDecimal()

        val achFee: BigDecimal
            get() = achFixedFeeUsd + fiatAmount * (achFeePercent / 100).toBigDecimal()

        val achFixedFeeUsd: BigDecimal
            get() = quoteResponse?.buyAchFees?.feeFixedUsd ?: BigDecimal.ZERO

        val achFeePercent: Float
            get() = quoteResponse?.buyAchFees?.feePercentage ?: 0f

        val cardFeePercent: Float
            get() = quoteResponse?.buyCardFeesPercent ?: 0f
    }
}