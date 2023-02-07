package com.rockwallet.buy.ui.features.input

import com.rockwallet.buy.ui.features.paymentmethod.PaymentMethodFragment
import com.rockwallet.common.ui.base.RockWalletEventHandler
import java.math.BigDecimal

interface BuyInputEventHandler: RockWalletEventHandler<BuyInputContract.Event> {

    override fun handleEvent(event: BuyInputContract.Event) {
        return when (event) {
            is BuyInputContract.Event.DismissClicked -> onDismissClicked()
            is BuyInputContract.Event.ContinueClicked -> onContinueClicked()
            is BuyInputContract.Event.QuoteTimeoutRetry -> onQuoteTimeoutRetry()
            is BuyInputContract.Event.AchPaymentsClicked -> onAchPaymentsClicked()
            is BuyInputContract.Event.PaymentMethodClicked -> onPaymentMethodClicked()
            is BuyInputContract.Event.CryptoCurrencyClicked -> onCryptoCurrencyClicked()
            is BuyInputContract.Event.FiatAmountChange -> onFiatAmountChanged(event.amount, true)
            is BuyInputContract.Event.CryptoAmountChange -> onCryptoAmountChanged(event.amount, true)
            is BuyInputContract.Event.CryptoCurrencyChanged -> onCryptoCurrencyChanged(event.currencyCode)
            is BuyInputContract.Event.PaymentMethodResultReceived -> onPaymentMethodResultReceived(event.result)
            is BuyInputContract.Event.PlaidResultEvent -> onPlaidResultEvent(event)
            is BuyInputContract.Event.PlaidResultError -> onPlaidResultError(event)
            is BuyInputContract.Event.PlaidResultSuccess -> onPlaidResultSuccess(event)
            is BuyInputContract.Event.SegmentedButtonClicked -> onSegmentedButtonClicked(event)
        }
    }

    fun onDismissClicked()

    fun onContinueClicked()

    fun onAchPaymentsClicked()

    fun onPaymentMethodClicked()

    fun onCryptoCurrencyClicked()

    fun onQuoteTimeoutRetry()

    fun onCryptoCurrencyChanged(currencyCode: String)

    fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean)

    fun onCryptoAmountChanged(cryptoAmount: BigDecimal, changeByUser: Boolean)

    fun onPaymentMethodResultReceived(result: PaymentMethodFragment.Result)

    fun onPlaidResultEvent(result: BuyInputContract.Event.PlaidResultEvent)

    fun onPlaidResultError(result: BuyInputContract.Event.PlaidResultError)

    fun onPlaidResultSuccess(result: BuyInputContract.Event.PlaidResultSuccess)

    fun onSegmentedButtonClicked(event: BuyInputContract.Event.SegmentedButtonClicked)
}