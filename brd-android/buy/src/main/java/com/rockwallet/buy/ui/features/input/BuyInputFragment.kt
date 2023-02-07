package com.rockwallet.buy.ui.features.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadwallet.tools.util.Utils.hideKeyboard
import com.breadwallet.util.formatFiatForUi
import com.breadwallet.util.formatFiatForUiAdvanced
import com.rockwallet.buy.R
import com.rockwallet.buy.databinding.FragmentBuyInputBinding
import com.rockwallet.buy.ui.features.addcard.AddCardFlow
import com.rockwallet.buy.ui.features.orderpreview.OrderPreviewFragment
import com.rockwallet.buy.ui.features.paymentmethod.PaymentMethodFragment
import com.rockwallet.buy.ui.features.timeout.PaymentTimeoutFragment
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.RockWalletToastUtil
import com.rockwallet.trade.ui.customview.CurrencyInputView
import com.rockwallet.trade.ui.features.assetselection.AssetSelectionFragment
import com.plaid.link.OpenPlaidLink
import com.plaid.link.Plaid
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal

class BuyInputFragment : Fragment(),
    RockWalletView<BuyInputContract.State, BuyInputContract.Effect> {

    private lateinit var binding: FragmentBuyInputBinding
    private val viewModel: BuyInputViewModel by viewModels()

    private val plaidSdkHandler = registerForActivityResult(OpenPlaidLink()) { result ->
        when (result) {
            is LinkSuccess ->
                viewModel.setEvent(
                    BuyInputContract.Event.PlaidResultSuccess(result)
                )
            is LinkExit ->
                viewModel.setEvent(
                    BuyInputContract.Event.PlaidResultError(result)
                )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buy_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyInputBinding.bind(view)

        with(binding) {
            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(BuyInputContract.Event.DismissClicked)
            }

            viewCryptoInput.setCallback(object : CurrencyInputView.Callback {
                override fun onCurrencySelectorClicked() {
                    viewModel.setEvent(BuyInputContract.Event.CryptoCurrencyClicked)
                }

                override fun onFiatAmountChanged(amount: BigDecimal) {
                    viewModel.setEvent(BuyInputContract.Event.FiatAmountChange(amount))
                }

                override fun onCryptoAmountChanged(amount: BigDecimal) {
                    viewModel.setEvent(BuyInputContract.Event.CryptoAmountChange(amount))
                }
            })

            btnContinue.setOnClickListener {
                hideKeyboard(binding.root.context)
                viewModel.setEvent(BuyInputContract.Event.ContinueClicked)
            }

            cvCreditCard.setOnClickListener {
                viewModel.setEvent(BuyInputContract.Event.PaymentMethodClicked)
            }

            viewAchPayment.setOnClickListener {
                viewModel.setEvent(BuyInputContract.Event.AchPaymentsClicked)
            }

            segmentBuyWithCard.setOnClickListener {
                viewModel.setEvent(BuyInputContract.Event.SegmentedButtonClicked(BuyInputContract.ScreenType.BUY_WITH_CARD))
            }

            segmentFundWithAch.setOnClickListener {
                viewModel.setEvent(BuyInputContract.Event.SegmentedButtonClicked(BuyInputContract.ScreenType.FUND_WITH_ACH))
            }

            Plaid.setLinkEventListener { event ->
                viewModel.setEvent(BuyInputContract.Event.PlaidResultEvent(event))
            }
        }

        // collect UI state
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                render(it)
            }
        }

        // collect UI effect
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                handleEffect(it)
            }
        }

        // listen for origin currency changes
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_CRYPTO_SELECTION,
            this
        ) { _, bundle ->
            val currency = bundle.getString(AssetSelectionFragment.EXTRA_SELECTED_CURRENCY)
            if (currency != null) {
                viewModel.setEvent(BuyInputContract.Event.CryptoCurrencyChanged(currency))
            }
        }

        // listen for destination currency changes
        parentFragmentManager.setFragmentResultListener(PaymentMethodFragment.REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getParcelable(PaymentMethodFragment.RESULT_KEY) as PaymentMethodFragment.Result?
            result?.let { viewModel.setEvent(BuyInputContract.Event.PaymentMethodResultReceived(it)) }
        }

        // listen for payment timeout callback
        parentFragmentManager.setFragmentResultListener(PaymentTimeoutFragment.REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(PaymentTimeoutFragment.RESULT_KEY)
            if (result == PaymentTimeoutFragment.RESULT_TRY_AGAIN) {
                viewModel.setEvent(BuyInputContract.Event.QuoteTimeoutRetry)
            }
        }

        // listen for order preview callback
        parentFragmentManager.setFragmentResultListener(OrderPreviewFragment.REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(OrderPreviewFragment.RESULT_KEY)
            if (result == OrderPreviewFragment.RESULT_NEW_QUOTE) {
                viewModel.setEvent(BuyInputContract.Event.QuoteTimeoutRetry)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            //User shouldn't be allowed to go back
        }
    }

    override fun render(state: BuyInputContract.State) {
        when (state) {
            is BuyInputContract.State.Error ->
                handleErrorState(state)

            is BuyInputContract.State.Loading ->
                handleLoadingState(state)

            is BuyInputContract.State.Loaded ->
                handleLoadedState(state)
        }
    }

    override fun handleEffect(effect: BuyInputContract.Effect) {
        when (effect) {
            is BuyInputContract.Effect.Dismiss -> requireActivity().let {
                it.setResult(effect.result)
                it.finish()
            }

            BuyInputContract.Effect.AddCard ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionAddCard(
                        AddCardFlow.BUY
                    )
                )

            BuyInputContract.Effect.PlaidConnectionError ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionPlaidConnectionError()
                )

            is BuyInputContract.Effect.ConnectBankAccount -> {
                val tokenConfiguration = LinkTokenConfiguration.Builder()
                    .token(effect.link)
                    .build()

                plaidSdkHandler.launch(tokenConfiguration)
            }

            is BuyInputContract.Effect.PaymentMethodSelection ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionPaymentMethod(
                        AddCardFlow.BUY
                    )
                )

            is BuyInputContract.Effect.OpenOrderPreview ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionOrderPreview(
                        networkFee = effect.networkFee,
                        fiatAmount = effect.fiatAmount,
                        fiatCurrency = effect.fiatCurrency,
                        quoteResponse = effect.quoteResponse,
                        cryptoCurrency = effect.cryptoCurrency,
                        paymentInstrument = effect.paymentInstrument
                    )
                )

            is BuyInputContract.Effect.CryptoSelection ->
                findNavController().navigate(
                    BuyInputFragmentDirections.actionAssetSelection(
                        requestKey = REQUEST_KEY_CRYPTO_SELECTION,
                        currencies = effect.currencies.toTypedArray(),
                        title = getString(R.string.Buy_iWant)
                    )
                )

            is BuyInputContract.Effect.ShowToast ->
                RockWalletToastUtil.showInfo(binding.root, effect.message)

            is BuyInputContract.Effect.ShowError ->
                RockWalletToastUtil.showError(binding.root, effect.message)

            is BuyInputContract.Effect.UpdateFiatAmount ->
                binding.viewCryptoInput.setFiatAmount(effect.amount, effect.changeByUser)

            is BuyInputContract.Effect.UpdateCryptoAmount ->
                binding.viewCryptoInput.setCryptoAmount(effect.amount, effect.changeByUser)
        }
    }

    private fun handleErrorState(state: BuyInputContract.State.Error) {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = false
        }
    }

    private fun handleLoadingState(state: BuyInputContract.State.Loading) {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = true
        }
    }

    private fun handleLoadedState(state: BuyInputContract.State.Loaded) {
        with(binding) {
            btnContinue.isEnabled = state.continueButtonEnabled
            segmentedControlContainer.isVisible = state.isAchAvailable

            tvRateValue.text = RATE_FORMAT.format(
                state.cryptoCurrency,
                state.oneCryptoUnitToFiatRate.formatFiatForUiAdvanced(
                    currencyCode = state.fiatCurrency,
                    showCurrencyName = true
                )
            )

            binding.viewCreditCard.isVisible = state.selectedPaymentMethod != null
            binding.tvSelectPaymentMethod.isVisible = state.selectedPaymentMethod == null

            state.selectedPaymentMethod.let {
                viewCreditCard.setPaymentInstrument(if (it is PaymentInstrument.Card) it else null)
                viewAchPayment.setPaymentInstrument(if (it is PaymentInstrument.BankAccount) it else null)
            }

            viewCryptoInput.setFiatCurrency(state.fiatCurrency)
            viewCryptoInput.setCryptoCurrency(state.cryptoCurrency)

            content.isVisible = true
            initialLoadingIndicator.isVisible = false
            quoteLoadingIndicator.isVisible = state.rateLoadingVisible
            fullScreenLoadingView.root.isVisible = state.fullScreenLoadingVisible

            tvRateValue.isInvisible = state.rateLoadingVisible || state.quoteResponse == null

            val minAmount = state.quoteResponse?.minimumValueUsd ?: BigDecimal.ZERO
            val minText = minAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true,
                showCurrencySymbol = true
            )

            val maxAmount = state.allowanceDaily
            val maxText = maxAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true,
                showCurrencySymbol = true
            )

            tvLimitsDisclaimer.text = when (state.screenType) {
                BuyInputContract.ScreenType.BUY_WITH_CARD -> {
                    getString(R.string.Buy_BuyLimits, minText, maxText)
                }
                BuyInputContract.ScreenType.FUND_WITH_ACH -> {
                    val lifetimeAmount = state.profile?.exchangeLimits?.buyAchAllowanceLifetime ?: BigDecimal.ZERO
                    val lifetimeText = lifetimeAmount.formatFiatForUi(
                        currencyCode = state.fiatCurrency,
                        showCurrencyName = true,
                        showCurrencySymbol = true
                    )
                    getString(R.string.Buy_achLimits, minText, maxText, lifetimeText)
                }
            }

            viewCryptoInput.setSelectionEnabled(state.canChangeAsset)

            segmentBuyWithCard.isSelected = state.screenType == BuyInputContract.ScreenType.BUY_WITH_CARD
            segmentFundWithAch.isSelected = state.screenType == BuyInputContract.ScreenType.FUND_WITH_ACH

            viewAchPayment.isVisible = state.screenType == BuyInputContract.ScreenType.FUND_WITH_ACH
            cvCreditCard.isVisible = state.screenType == BuyInputContract.ScreenType.BUY_WITH_CARD
        }
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %s"
        const val REQUEST_KEY_CRYPTO_SELECTION = "req_code_crypto_select"
    }
}