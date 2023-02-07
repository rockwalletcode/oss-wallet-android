package com.rockwallet.buy.ui.features.input

import android.app.Application
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.core.text.bold
import androidx.lifecycle.viewModelScope
import com.breadwallet.ext.isZero
import com.breadwallet.platform.interfaces.AccountMetaDataProvider
import com.breadwallet.tools.security.ProfileManager
import com.breadwallet.tools.util.TokenUtil
import com.rockwallet.buy.R
import com.rockwallet.buy.data.BuyApi
import com.rockwallet.buy.ui.BuyActivity
import com.rockwallet.buy.ui.features.paymentmethod.PaymentMethodFragment
import com.rockwallet.buy.utils.SpannableHelper
import com.rockwallet.common.data.model.isKyc2
import com.rockwallet.common.data.Status
import com.rockwallet.common.data.enums.PaymentInstrumentType
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.getString
import com.rockwallet.trade.data.enums.QuoteType
import com.rockwallet.trade.utils.EstimateReceivingFee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.math.BigDecimal

class BuyInputViewModel(
    application: Application
) : RockWalletViewModel<BuyInputContract.State, BuyInputContract.Event, BuyInputContract.Effect>(
    application
), BuyInputEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()
    private val estimateFee by kodein.instance<EstimateReceivingFee>()
    private val profileManager by kodein.instance<ProfileManager>()
    private val metaDataManager by kodein.instance<AccountMetaDataProvider>()

    private val currentFiatCurrency = "USD"

    private val currentLoadedState: BuyInputContract.State.Loaded?
        get() = state.value as BuyInputContract.State.Loaded?

    init {
        loadInitialData()
    }

    override fun createInitialState() = BuyInputContract.State.Loading

    override fun onDismissClicked() {
        setEffect { BuyInputContract.Effect.Dismiss() }
    }

    override fun onQuoteTimeoutRetry() {
        requestNewQuote()
    }

    override fun onSegmentedButtonClicked(event: BuyInputContract.Event.SegmentedButtonClicked) {
        val newState = currentLoadedState?.let { state ->
            state.copy(
                screenType = event.screenType,
                canChangeAsset = event.screenType == BuyInputContract.ScreenType.BUY_WITH_CARD || ACH_SUPPORTED_CURRENCIES.size > 1,
                selectedPaymentMethod = state.paymentInstruments.lastOrNull {
                    when (it) {
                        is PaymentInstrument.Card ->
                            event.screenType == BuyInputContract.ScreenType.BUY_WITH_CARD
                        is PaymentInstrument.BankAccount ->
                            event.screenType == BuyInputContract.ScreenType.FUND_WITH_ACH
                    }
                },
            )
        }

        newState?.let {
            setState { it }
            onCryptoCurrencyChanged(it.cryptoCurrency)
            checkIfAchWalletDisabled(newState)
        }
    }

    override fun onCryptoCurrencyChanged(currencyCode: String) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val isWalletEnabled = isWalletEnabled(currencyCode)

            setState {
                state.copy(
                    isSelectedWalletEnabled = isWalletEnabled,
                    cryptoAmount = BigDecimal.ZERO,
                    fiatAmount = BigDecimal.ZERO,
                    quoteResponse = null,
                    networkFee = null,
                    selectedAchCryptoCurrency = when (state.screenType) {
                        BuyInputContract.ScreenType.FUND_WITH_ACH -> currencyCode
                        else -> state.selectedAchCryptoCurrency
                    },
                    selectedBuyCryptoCurrency = when (state.screenType) {
                        BuyInputContract.ScreenType.BUY_WITH_CARD -> currencyCode
                        else -> state.selectedBuyCryptoCurrency
                    }
                ).validate()
            }

            updateAmounts(
                fiatAmountChangedByUser = false,
                cryptoAmountChangedByUser = false,
            )

            requestNewQuote()
        }
    }

    override fun onPaymentMethodResultReceived(result: PaymentMethodFragment.Result) {
        when (result) {
            is PaymentMethodFragment.Result.Selected -> {
                val state = currentLoadedState ?: return
                setState { state.copy(selectedPaymentMethod = result.paymentInstrument).validate() }
            }

            is PaymentMethodFragment.Result.Cancelled -> if (result.dataUpdated) {
                refreshPaymentInstruments()
            }
        }
    }

    override fun onPlaidResultEvent(result: BuyInputContract.Event.PlaidResultEvent) {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { buyApi.postPlaidEvent(result.event) },
            callback = {
                // empty
            }
        )
    }

    override fun onPlaidResultError(result: BuyInputContract.Event.PlaidResultError) {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { buyApi.postPlaidError(result.result) },
            callback = {
                // empty
            }
        )

        setEffect { BuyInputContract.Effect.PlaidConnectionError }
    }

    override fun onPlaidResultSuccess(result: BuyInputContract.Event.PlaidResultSuccess) {
        val state = currentLoadedState ?: return

        callApi(
            endState = { currentState },
            startState = { state.copy(fullScreenLoadingVisible = true) },
            action = { buyApi.postPlaidToken(result.result) },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        setEffect {
                            BuyInputContract.Effect.ShowToast(
                                getString(R.string.Buy_achPaymentMethodAdded)
                            )
                        }
                        refreshAchPaymentInstruments()
                    }

                    Status.ERROR -> {
                        currentLoadedState?.let {
                            setState { it.copy(fullScreenLoadingVisible = false) }
                        }

                        setEffect {
                            BuyInputContract.Effect.ShowError(
                                it.message ?: getString(R.string.ErrorMessages_default)
                            )
                        }
                    }
                }
            }
        )
    }

    override fun onCryptoCurrencyClicked() {
        val state = currentLoadedState ?: return
        setEffect { BuyInputContract.Effect.CryptoSelection(state.supportedCurrencies) }
    }

    override fun onAchPaymentsClicked() {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { buyApi.getPlaidLink() },
            callback = {
                val link = it.data?.linkToken

                setEffect {
                    if (link.isNullOrEmpty() || it.status == Status.ERROR) {
                        BuyInputContract.Effect.ShowError(
                            it.message ?: getString(R.string.ErrorMessages_default)
                        )
                    } else {
                        BuyInputContract.Effect.ConnectBankAccount(link)
                    }
                }
            }
        )
    }

    override fun onPaymentMethodClicked() {
        val paymentMethods = currentLoadedState?.paymentInstruments ?: return

        setEffect {
            if (paymentMethods.isEmpty()) {
                BuyInputContract.Effect.AddCard
            } else {
                BuyInputContract.Effect.PaymentMethodSelection(paymentMethods)
            }
        }
    }

    override fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        onAmountChanged(
            state = state,
            fiatAmount = fiatAmount,
            cryptoAmount = fiatAmount * state.oneFiatUnitToCryptoRate,
            cryptoAmountChangeByUser = false,
            fiatAmountChangeByUser = changeByUser
        )
    }

    override fun onCryptoAmountChanged(cryptoAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        onAmountChanged(
            state = state,
            fiatAmount = cryptoAmount * state.oneCryptoUnitToFiatRate,
            cryptoAmount = cryptoAmount,
            cryptoAmountChangeByUser = changeByUser,
            fiatAmountChangeByUser = false
        )
    }

    private fun onAmountChanged(state: BuyInputContract.State.Loaded, cryptoAmount: BigDecimal, fiatAmount: BigDecimal, cryptoAmountChangeByUser: Boolean, fiatAmountChangeByUser: Boolean) {
        val networkFee = estimateFee(
            quote = state.quoteResponse,
            amount = cryptoAmount,
            fiatCode = state.fiatCurrency,
            walletCurrency = state.cryptoCurrency
        )

        setState {
            state.copy(
                networkFee = networkFee,
                fiatAmount = fiatAmount,
                cryptoAmount = cryptoAmount,
            ).validate()
        }

        updateAmounts(
            fiatAmountChangedByUser = fiatAmountChangeByUser,
            cryptoAmountChangedByUser = cryptoAmountChangeByUser,
        )
    }

    override fun onContinueClicked() {
        val state = currentLoadedState ?: return
        val networkFee = state.networkFee ?: return
        val quoteResponse = state.quoteResponse ?: return
        val paymentInstrument = state.selectedPaymentMethod ?: return

        val validationError = validate(state)
        if (validationError != null) {
            setState { state.copy(continueButtonEnabled = false) }
            setEffect {
                BuyInputContract.Effect.ShowError(
                    validationError.toString(getApplication<Application?>().applicationContext)
                )
            }
            return
        }

        setEffect {
            BuyInputContract.Effect.OpenOrderPreview(
                networkFee = networkFee,
                fiatAmount = state.fiatAmount,
                fiatCurrency = state.fiatCurrency,
                quoteResponse = quoteResponse,
                cryptoCurrency = state.cryptoCurrency,
                paymentInstrument = paymentInstrument
            )
        }
    }

    private fun updateAmounts(fiatAmountChangedByUser: Boolean, cryptoAmountChangedByUser: Boolean) {
        val state = currentLoadedState ?: return

        setEffect { BuyInputContract.Effect.UpdateFiatAmount(state.fiatAmount, fiatAmountChangedByUser) }
        setEffect { BuyInputContract.Effect.UpdateCryptoAmount(state.cryptoAmount, cryptoAmountChangedByUser) }
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val instrumentsResponse = buyApi.getPaymentInstruments()
            val supportedCurrencies = buyApi.getSupportedCurrencies().data ?: emptyList()

            if (instrumentsResponse.status == Status.ERROR || supportedCurrencies.isEmpty()) {
                showErrorState()
                return@launch
            }

            val firstWallet = supportedCurrencies.firstOrNull { isWalletEnabled(it) }

            val quoteResponse = firstWallet?.let {
                buyApi.getQuote(
                    from = currentFiatCurrency,
                    to = it,
                    type = QuoteType.BUY_CARD
                )
            }

            if (quoteResponse == null || quoteResponse.status == Status.ERROR) {
                showErrorState()
                return@launch
            }

            setState {
                BuyInputContract.State.Loaded(
                    quoteResponse = requireNotNull(quoteResponse.data),
                    fiatCurrency = currentFiatCurrency,
                    selectedAchCryptoCurrency = ACH_SUPPORTED_CURRENCIES.first(),
                    selectedBuyCryptoCurrency = firstWallet,
                    supportedCurrencies = supportedCurrencies,
                    paymentInstruments = instrumentsResponse.data ?: emptyList(),
                    selectedPaymentMethod = instrumentsResponse.data?.lastOrNull {
                        it.type == PaymentInstrumentType.CREDIT_CARD
                    },
                    profile = profileManager.getProfile()
                )
            }
        }
    }

    private fun checkIfAchWalletDisabled(newState: BuyInputContract.State.Loaded) {
        if (newState.screenType != BuyInputContract.ScreenType.FUND_WITH_ACH) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val token = TokenUtil.tokenForCode(newState.cryptoCurrency)
            if (token?.isSupported == true && !isWalletEnabled(newState.cryptoCurrency)) {
                val linkText = getString(R.string.Buy_Ach_WalletDisabled_Link)
                val boldText = getString(R.string.Buy_Ach_WalletDisabled_ManageAssets)
                val fullText = getString(R.string.Buy_Ach_WalletDisabled, token.symbol.uppercase(), linkText, boldText)

                val spannable = SpannableHelper.clickableText(
                    fullText = fullText,
                    linkText = linkText,
                    boldTexts = arrayOf(linkText, boldText),
                    action = {
                        setEffect {
                            BuyInputContract.Effect.Dismiss(BuyActivity.RESULT_OPEN_MANAGE_ASSETS)
                        }
                    }
                )

                setEffect {
                    BuyInputContract.Effect.ShowToast(spannable)
                }
            }
        }
    }

    private fun refreshPaymentInstruments(callback: (BuyInputContract.State.Loaded) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val instrumentsResponse = buyApi.getPaymentInstruments().data ?: emptyList()

            val currentState = currentLoadedState ?: return@launch
            val isSelectedMethodInList = instrumentsResponse.any {
                when {
                    it is PaymentInstrument.Card && currentState.selectedPaymentMethod is PaymentInstrument.Card ->
                        it.id == currentState.selectedPaymentMethod.id
                    it is PaymentInstrument.BankAccount && currentState.selectedPaymentMethod is PaymentInstrument.BankAccount ->
                        it.id == currentState.selectedPaymentMethod.id
                    else -> false
                }
            }

            // remove selected payment method if it was removed
            val newState = if (!isSelectedMethodInList) {
                currentState.copy(
                    selectedPaymentMethod = null,
                    paymentInstruments = instrumentsResponse,
                    fullScreenLoadingVisible = false
                )
            } else {
                currentState.copy(
                    paymentInstruments = instrumentsResponse,
                    fullScreenLoadingVisible = false
                )
            }

            setState { newState }
            callback(newState)
        }
    }

    private fun refreshAchPaymentInstruments() {
        refreshPaymentInstruments { currentState ->
            val paymentMethod = currentState.selectedPaymentMethod

            if (paymentMethod == null || paymentMethod.type != PaymentInstrumentType.BANK_ACCOUNT) {
                val achPaymentMethod = currentState.paymentInstruments.lastOrNull {
                    it.type == PaymentInstrumentType.BANK_ACCOUNT
                }

                setState { currentState.copy(selectedPaymentMethod = achPaymentMethod).validate() }
            }
        }
    }

    private fun requestNewQuote() {
        viewModelScope.launch {
            val state = currentLoadedState ?: return@launch
            setState { state.copy(rateLoadingVisible = true) }

            val response = buyApi.getQuote(
                from = state.fiatCurrency,
                to = state.cryptoCurrency,
                type = state.quoteType
            )
            when (response.status) {
                Status.SUCCESS -> {
                    val latestState = currentLoadedState ?: return@launch
                    val responseData = requireNotNull(response.data)

                    setState {
                        latestState.copy(
                            rateLoadingVisible = false,
                            quoteResponse = responseData
                        )
                    }

                    onFiatAmountChanged(
                        fiatAmount = latestState.fiatAmount,
                        changeByUser = false
                    )
                }
                Status.ERROR -> {
                    val latestState = currentLoadedState ?: return@launch

                    setState {
                        latestState.copy(
                            rateLoadingVisible = false,
                            quoteResponse = null
                        )
                    }

                    setEffect {
                        BuyInputContract.Effect.ShowError(
                            getString(R.string.ErrorMessages_NetworkIssues)
                        )
                    }
                }
            }
        }
    }

    private fun validate(state: BuyInputContract.State.Loaded) = when {
        state.profile?.isKyc2() == false ->
            BuyInputContract.ErrorMessage.Kyc2Required
        state.networkFee == null ->
            BuyInputContract.ErrorMessage.NetworkIssues
        state.fiatAmount < state.minFiatAmount ->
            BuyInputContract.ErrorMessage.MinBuyAmount(state.minFiatAmount, state.fiatCurrency)
        state.fiatAmount > state.maxFiatAmount ->
            BuyInputContract.ErrorMessage.MaxBuyAmount(state.maxFiatAmount, state.fiatCurrency)
        state.fiatAmount > state.availableDailyLimit ->
            BuyInputContract.ErrorMessage.DailyLimit(state.dailyLimit, state.fiatCurrency)
        state.fiatAmount > state.availableLifetimeLimit ->
            BuyInputContract.ErrorMessage.LifetimeLimit(state.lifetimeLimit, state.fiatCurrency)
        else -> null
    }

    private suspend fun isWalletEnabled(currencyCode: String): Boolean {
        val enabledWallets = metaDataManager.enabledWallets().first()
        val token = TokenUtil.tokenForCode(currencyCode) ?: return false
        return token.isSupported && enabledWallets.contains(token.currencyId)
    }

    private fun showErrorState() {
        setState { BuyInputContract.State.Error }
        setEffect {
            BuyInputContract.Effect.ShowError(
                getString(R.string.ErrorMessages_NetworkIssues)
            )
        }
    }

    private fun BuyInputContract.State.Loaded.validate() = copy(
        continueButtonEnabled = !cryptoAmount.isZero()
                && !fiatAmount.isZero()
                && networkFee != null
                && quoteResponse != null
                && selectedPaymentMethod != null
                && isSelectedWalletEnabled
    )

    private companion object {
        val ACH_SUPPORTED_CURRENCIES = arrayOf("usdc")
    }
}