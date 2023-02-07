package com.rockwallet.buy.ui.features.input

import android.app.Activity
import android.content.Context
import com.breadwallet.util.formatFiatForUi
import com.plaid.link.event.LinkEvent
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import com.rockwallet.buy.R
import com.rockwallet.buy.ui.features.paymentmethod.PaymentMethodFragment
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.data.model.Profile
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.trade.data.enums.QuoteType
import com.rockwallet.trade.data.model.FeeAmountData
import com.rockwallet.trade.data.response.QuoteResponse
import java.math.BigDecimal
import java.math.RoundingMode

interface BuyInputContract {

    sealed class Event : RockWalletContract.Event {
        object DismissClicked : Event()
        object ContinueClicked : Event()
        object PaymentMethodClicked : Event()
        object AchPaymentsClicked : Event()
        object CryptoCurrencyClicked : Event()
        object QuoteTimeoutRetry : Event()

        data class FiatAmountChange(val amount: BigDecimal) : Event()
        data class CryptoAmountChange(val amount: BigDecimal) : Event()
        data class CryptoCurrencyChanged(val currencyCode: String) : Event()
        data class PaymentMethodResultReceived(val result: PaymentMethodFragment.Result) : Event()
        data class PlaidResultEvent(val event: LinkEvent) : Event()
        data class PlaidResultError(val result: LinkExit) : Event()
        data class PlaidResultSuccess(val result: LinkSuccess) : Event()
        data class SegmentedButtonClicked(val screenType: ScreenType): Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object AddCard : Effect()
        object PlaidConnectionError : Effect()

        data class Dismiss(val result: Int = Activity.RESULT_CANCELED) : Effect()
        data class ConnectBankAccount(val link: String) : Effect()
        data class PaymentMethodSelection(val paymentInstruments: List<PaymentInstrument>) : Effect()
        data class ShowToast(val message: CharSequence) : Effect()
        data class ShowError(val message: String) : Effect()
        data class CryptoSelection(val currencies: List<String>) : Effect()
        data class UpdateFiatAmount(val amount: BigDecimal, val changeByUser: Boolean) : Effect()
        data class UpdateCryptoAmount(val amount: BigDecimal, val changeByUser: Boolean) : Effect()

        data class OpenOrderPreview(
            val networkFee: FeeAmountData,
            val fiatAmount: BigDecimal,
            val fiatCurrency: String,
            val cryptoCurrency: String,
            val quoteResponse: QuoteResponse,
            val paymentInstrument: PaymentInstrument
        ) : Effect()
    }

    sealed class State : RockWalletContract.State {
        object Error : State()
        object Loading : State()
        data class Loaded(
            val screenType: ScreenType = ScreenType.BUY_WITH_CARD,
            val supportedCurrencies: List<String>,
            val quoteResponse: QuoteResponse?,
            val paymentInstruments: List<PaymentInstrument>,
            val selectedPaymentMethod: PaymentInstrument? = null,
            val networkFee: FeeAmountData? = null,
            val fiatCurrency: String,
            val selectedBuyCryptoCurrency: String,
            val selectedAchCryptoCurrency: String,
            val fiatAmount: BigDecimal = BigDecimal.ZERO,
            val cryptoAmount: BigDecimal = BigDecimal.ZERO,
            val canChangeAsset: Boolean = true,
            val rateLoadingVisible: Boolean = false,
            val continueButtonEnabled: Boolean = false,
            val fullScreenLoadingVisible: Boolean = false,
            val isSelectedWalletEnabled: Boolean = true,
            val profile: Profile?
        ) : State() {
            val quoteType: QuoteType
                get() = when (screenType) {
                    ScreenType.BUY_WITH_CARD -> QuoteType.BUY_CARD
                    ScreenType.FUND_WITH_ACH -> QuoteType.BUY_ACH
                }
            val minFiatAmount: BigDecimal
                get() = quoteResponse?.minimumValueUsd ?: BigDecimal.ZERO
            val maxFiatAmount: BigDecimal
                get() = quoteResponse?.maximumValueUsd ?: BigDecimal.ZERO
            val oneFiatUnitToCryptoRate: BigDecimal
                get() = quoteResponse?.exchangeRate ?: BigDecimal.ZERO
            val oneCryptoUnitToFiatRate: BigDecimal
                get() = BigDecimal.ONE.divide((quoteResponse?.exchangeRate ?: BigDecimal.ONE), 20, RoundingMode.HALF_UP) ?: BigDecimal.ZERO
            val dailyLimit: BigDecimal
                get() = when (screenType) {
                    ScreenType.BUY_WITH_CARD -> profile?.exchangeLimits?.buyAllowanceDaily
                    ScreenType.FUND_WITH_ACH -> profile?.exchangeLimits?.buyAchAllowanceDaily
                } ?: BigDecimal.ZERO
            val availableDailyLimit: BigDecimal
                get() = when (screenType) {
                    ScreenType.BUY_WITH_CARD -> profile?.exchangeLimits?.availableBuyDailyLimit
                    ScreenType.FUND_WITH_ACH -> profile?.exchangeLimits?.availableBuyAchDailyLimit
                } ?: BigDecimal.ZERO
            val lifetimeLimit: BigDecimal
                get() = when (screenType) {
                    ScreenType.BUY_WITH_CARD -> profile?.exchangeLimits?.buyAllowanceLifetime
                    ScreenType.FUND_WITH_ACH -> profile?.exchangeLimits?.buyAchAllowanceLifetime
                } ?: BigDecimal.ZERO
            val availableLifetimeLimit: BigDecimal
                get() = when (screenType) {
                    ScreenType.BUY_WITH_CARD -> profile?.exchangeLimits?.availableBuyLifetimeLimit
                    ScreenType.FUND_WITH_ACH -> profile?.exchangeLimits?.availableBuyAchLifetimeLimit
                } ?: BigDecimal.ZERO
            val allowanceDaily: BigDecimal
                get() = when (screenType) {
                    ScreenType.BUY_WITH_CARD -> profile?.exchangeLimits?.buyAllowanceDaily
                    ScreenType.FUND_WITH_ACH -> profile?.exchangeLimits?.buyAchAllowanceDaily
                } ?: BigDecimal.ZERO
            val cryptoCurrency: String
                get() = when (screenType) {
                    ScreenType.BUY_WITH_CARD -> selectedBuyCryptoCurrency
                    ScreenType.FUND_WITH_ACH -> selectedAchCryptoCurrency
                }
            val isAchAvailable: Boolean
                get() = profile?.accessRights?.achAccess == true
        }
    }

    sealed class ErrorMessage {

        abstract fun toString(context: Context): String

        object Kyc2Required : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_Kyc2AccessDenied
            )
        }

        object NetworkIssues : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_NetworkIssues
            )
        }

        class MinBuyAmount(private val minFiatAmount: BigDecimal, val fiatCurrency: String): ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_amountTooLow, minFiatAmount.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = false,
                    showCurrencySymbol = true
                ), fiatCurrency
            )
        }

        class MaxBuyAmount(private val maxFiatAmount: BigDecimal, private val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_AmountTooHigh, maxFiatAmount.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = false,
                    showCurrencySymbol = true
                ),fiatCurrency
            )
        }

        class DailyLimit(private val dailyLimit: BigDecimal, private val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_AmountTooHigh, dailyLimit.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = false,
                    showCurrencySymbol = true
                ),fiatCurrency
            )
        }

        class LifetimeLimit(private val lifetimeLimit: BigDecimal, private val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_overLifetimeLimitAch, lifetimeLimit.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = false,
                    showCurrencySymbol = true
                ),fiatCurrency
            )
        }
    }

    enum class ScreenType {
        BUY_WITH_CARD,
        FUND_WITH_ACH
    }
}