package com.rockwallet.buy.ui.features.buydetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.tools.manager.BRClipboardManager
import com.breadwallet.util.formatFiatForUi
import com.breadwallet.util.formatFiatForUiAdvanced
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.RockWalletToastUtil
import com.rockwallet.buy.R
import com.rockwallet.buy.data.enums.BuyDetailsFlow
import com.rockwallet.trade.data.response.ExchangeOrderStatus
import com.rockwallet.buy.databinding.FragmentBuyDetailsBinding
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.dialog.InfoDialog
import com.rockwallet.common.ui.dialog.InfoDialogArgs
import com.rockwallet.trade.data.response.ExchangeType
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

class BuyDetailsFragment : Fragment(),
    RockWalletView<BuyDetailsContract.State, BuyDetailsContract.Effect> {

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault())

    private lateinit var binding: FragmentBuyDetailsBinding
    private val viewModel: BuyDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_buy_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyDetailsBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.BackClicked)
            }
            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.DismissClicked)
            }
            tvOrderId.setOnClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.OrderIdClicked)
            }
            tvCryptoTransactionId.setOnClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.TransactionIdClicked)
            }
            ivInfoCredit.setOnClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.OnCreditInfoClicked)
            }
            ivInfoNetwork.setOnClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.OnNetworkInfoClicked)
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

        requireActivity().onBackPressedDispatcher.addCallback {
            //User shouldn't be allowed to go back
        }

        viewModel.setEvent(BuyDetailsContract.Event.LoadData)
    }

    override fun render(state: BuyDetailsContract.State) {
        when (state) {
            is BuyDetailsContract.State.Error ->
                showErrorState()

            is BuyDetailsContract.State.Loading ->
                showLoadingState()

            is BuyDetailsContract.State.Loaded ->
                showLoadedState(state)
        }
    }

    override fun handleEffect(effect: BuyDetailsContract.Effect) {
        when (effect) {
            BuyDetailsContract.Effect.Dismiss ->
                requireActivity().finish()

            is BuyDetailsContract.Effect.ShowToast ->
                RockWalletToastUtil.showInfo(
                    binding.root, effect.message
                )

            is BuyDetailsContract.Effect.CopyToClipboard ->
                copyToClipboard(effect.data)

            is BuyDetailsContract.Effect.ShowInfoDialog ->
                showInfoDialog(effect)
        }
    }

    private fun showLoadingState() {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = true
        }
    }

    private fun showErrorState() {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = false
        }
    }

    private fun showLoadedState(state: BuyDetailsContract.State.Loaded) {
        val data = state.data

        with(binding) {
            toolbar.setShowBackButton(state.flow == BuyDetailsFlow.TRANSACTIONS)
            toolbar.setShowDismissButton(state.flow == BuyDetailsFlow.PURCHASE)

            // status item
            icStatus.setImageResource(setStatusIcon(data.status))
            tvStatus.text = getString(setStatusTitle(data.status))

            // crypto amount item
            ivCrypto.postLoadIcon(data.destination.currency)
            tvCryptoAmount.text = data.destination.currencyAmount.formatCryptoForUi(
                data.destination.currency, 8
            )

            // purchase details items
            tvRateValue.text = RATE_FORMAT.format(
                data.destination.currency,
                state.fiatPriceForOneCryptoUnit.formatFiatForUiAdvanced(
                    showCurrencyName = true,
                    currencyCode = "USD"
                )
            )

            tvNetworkFeeAmount.text = state.networkFee.formatFiatForUi(
                currencyCode = "USD",
                showCurrencyName = true
            )

            tvAchFeeAmount.text = state.usdFee.formatFiatForUi(
                currencyCode = "USD",
                showCurrencyName = true
            )

            tvCreditFeeAmount.text = state.usdFee.formatFiatForUi(
                currencyCode = "USD",
                showCurrencyName = true
            )

            tvPurchasedAmount.text = state.purchasedAmount.formatFiatForUi(
                currencyCode = "USD",
                showCurrencyName = true
            )

            tvTotalAmount.text = state.data.source.currencyAmount.formatFiatForUi(
                currencyCode = state.data.source.currency,
                showCurrencyName = true
            )

            groupAchFee.isVisible = state.data.type == ExchangeType.BUY_ACH
            groupCardFee.isVisible = state.data.type == ExchangeType.BUY
            viewAchDetails.isVisible = state.data.type == ExchangeType.BUY_ACH
            viewCreditCard.isVisible = state.data.type == ExchangeType.BUY

            state.data.source.paymentInstrument?.let {
                when (it) {
                    is PaymentInstrument.Card -> {
                        viewCreditCard.setPaymentInstrument(it)
                    }
                    is PaymentInstrument.BankAccount -> {
                        viewAchDetails.setPaymentInstrument(it)
                    }
                }
            }

            // rock wallet transaction ID item
            tvOrderId.text = data.orderId

            // {cryptoCurrency} Transaction ID item
            tvCryptoTransactionIdTitle.text = getString(
                R.string.Buy_txHashHeader, data.destination.currency.uppercase()
            )

            if (data.destination.transactionId.isNullOrEmpty()) {
                tvCryptoTransactionId.text = getString(R.string.Transaction_pending)
                tvCryptoTransactionId.setCompoundDrawablesRelative(null, null, null, null)
            } else {
                tvCryptoTransactionId.text = data.destination.transactionId
            }

            state.achFeePercent?.let {
                tvAchFeeTitle.text = getString(R.string.Buy_achFee, it)
            }

            state.cardFeePercent?.let {
                tvCreditFeeTitle.text = "${getString(R.string.Swap_CardFee)} ($it)"
            }

            // timestamp item
            val date = Date(data.timestamp)
            tvTimestamp.text = dateFormatter.format(date)

            // others
            content.isVisible = true
            initialLoadingIndicator.isVisible = false
        }
    }

    private fun setStatusTitle(status: ExchangeOrderStatus): Int {
        return when (status) {
            ExchangeOrderStatus.PENDING -> R.string.Transaction_pending
            ExchangeOrderStatus.COMPLETE -> R.string.Transaction_complete
            ExchangeOrderStatus.FAILED -> R.string.Transaction_failed
            ExchangeOrderStatus.REFUNDED -> R.string.Transaction_refunded
            ExchangeOrderStatus.MANUALLY_SETTLED -> R.string.Transaction_ManuallySettled
        }
    }

    private fun setStatusIcon(status: ExchangeOrderStatus): Int {
        return when (status) {
            ExchangeOrderStatus.PENDING -> R.drawable.ic_status_pending
            ExchangeOrderStatus.FAILED -> R.drawable.ic_status_failed
            ExchangeOrderStatus.COMPLETE -> R.drawable.ic_status_complete
            ExchangeOrderStatus.REFUNDED -> R.drawable.ic_status_refunded
            ExchangeOrderStatus.MANUALLY_SETTLED -> R.drawable.ic_status_complete
        }
    }

    private fun copyToClipboard(data: String) {
        BRClipboardManager.putClipboard(data)

        RockWalletToastUtil.showInfo(
            binding.root, getString(R.string.Receive_copied)
        )
    }

    private fun showInfoDialog(effect: BuyDetailsContract.Effect.ShowInfoDialog) {
        val args = InfoDialogArgs(
            image = effect.image,
            title = effect.title,
            description = effect.description
        )

        InfoDialog(args).show(parentFragmentManager, InfoDialog.TAG)
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %s"
    }
}