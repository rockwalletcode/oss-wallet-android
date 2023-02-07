package com.rockwallet.trade.ui.features.swapdetails

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
import com.rockwallet.common.utils.viewScope
import com.rockwallet.trade.R
import com.rockwallet.trade.data.response.ExchangeOrderStatus
import com.rockwallet.trade.databinding.FragmentSwapDetailsBinding
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

class SwapDetailsFragment : Fragment(),
    RockWalletView<SwapDetailsContract.State, SwapDetailsContract.Effect> {

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault())

    private lateinit var binding: FragmentSwapDetailsBinding
    private val viewModel: SwapDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_swap_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSwapDetailsBinding.bind(view)

        with(binding) {
            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(SwapDetailsContract.Event.DismissClicked)
            }
            tvOrderId.setOnClickListener {
                viewModel.setEvent(SwapDetailsContract.Event.OrderIdClicked)
            }
            tvSwapFromId.setOnClickListener {
                viewModel.setEvent(SwapDetailsContract.Event.SourceTransactionIdClicked)
            }
            tvSwapToId.setOnClickListener {
                viewModel.setEvent(SwapDetailsContract.Event.DestinationTransactionIdClicked)
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

        viewModel.setEvent(SwapDetailsContract.Event.LoadData)
    }

    override fun render(state: SwapDetailsContract.State) {
        when (state) {
            is SwapDetailsContract.State.Error ->
                showErrorState()

            is SwapDetailsContract.State.Loading ->
                showLoadingState()

            is SwapDetailsContract.State.Loaded ->
                showLoadedState(state)
        }
    }

    override fun handleEffect(effect: SwapDetailsContract.Effect) {
        when (effect) {
            SwapDetailsContract.Effect.Dismiss ->
                requireActivity().finish()

            is SwapDetailsContract.Effect.ShowToast ->
                RockWalletToastUtil.showInfo(
                    binding.root, effect.message
                )

            is SwapDetailsContract.Effect.CopyToClipboard ->
                copyToClipboard(effect.data)
        }
    }

    private fun showErrorState() {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = false
        }
    }

    private fun showLoadingState() {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = true
        }
    }

    private fun showLoadedState(state: SwapDetailsContract.State.Loaded) {
        val data = state.data

        with(binding) {
            tvOrderId.text = data.orderId

            binding.root.post {
                tvSwapTo.text = getString(R.string.Swap_transactionTo, data.destination.currency.uppercase())
                tvSwapToIdTitle.text = getString(
                    R.string.Buy_txHashHeader, data.destination.currency.uppercase()
                )

                if (data.destination.transactionId.isNullOrEmpty()) {
                    tvSwapToId.text = getString(R.string.Transaction_pending)
                    tvSwapToId.setCompoundDrawablesRelative(null, null, null, null)
                } else {
                    tvSwapToId.text = data.destination.transactionId
                }

                icSwapTo.loadIcon(
                    scope = tvSwapFrom.viewScope,
                    currencyCode = data.destination.currency
                )

                val formatFiatTo = data.destination.usdAmount?.formatFiatForUiAdvanced(
                    currencyCode = "USD",
                    showCurrencyName = true
                ) ?: "? USD"
                val formatCryptoTo = data.destination.currencyAmount.formatCryptoForUi(null)
                tvToCurrencyValue.text = "$formatCryptoTo / $formatFiatTo"

                tvSwapFrom.text = getString(R.string.Swap_transactionFrom, data.source.currency.uppercase())
                tvSwapFromIdTitle.text = getString(
                    R.string.Buy_txHashHeader, data.source.currency.uppercase()
                )

                if (data.source.transactionId.isNullOrEmpty()) {
                    tvSwapFromId.text = getString(R.string.Transaction_pending)
                    tvSwapFromId.setCompoundDrawablesRelative(null, null, null, null)
                } else {
                    tvSwapFromId.text = data.source.transactionId
                }

                icSwapFrom.loadIcon(
                    scope = tvSwapFrom.viewScope,
                    currencyCode = data.source.currency
                )

                val formatFiatFrom = data.source.usdAmount?.formatFiatForUiAdvanced(
                    currencyCode = "USD",
                    showCurrencyName = true
                ) ?: "? USD"
                val formatCryptoFrom = data.source.currencyAmount.formatCryptoForUi(null)
                tvFromCurrencyValue.text = "$formatCryptoFrom / $formatFiatFrom"
            }

            val date = Date(data.timestamp)
            tvTimestamp.text = dateFormatter.format(date)

            icStatus.setImageResource(setStatusIcon(data.status))
            tvStatus.text = getString(setStatusTitle(data.status))

            content.isVisible = true
            initialLoadingIndicator.isVisible = false
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

    private fun setStatusTitle(status: ExchangeOrderStatus): Int {
        return when (status) {
            ExchangeOrderStatus.PENDING -> R.string.Transaction_pending
            ExchangeOrderStatus.COMPLETE -> R.string.Transaction_complete
            ExchangeOrderStatus.FAILED -> R.string.Transaction_failed
            ExchangeOrderStatus.REFUNDED -> R.string.Transaction_refunded
            ExchangeOrderStatus.MANUALLY_SETTLED ->R.string.Transaction_ManuallySettled
        }
    }

    private fun copyToClipboard(data: String) {
        BRClipboardManager.putClipboard(data)

        RockWalletToastUtil.showInfo(
            binding.root, getString(R.string.Receive_copied)
        )
    }
}