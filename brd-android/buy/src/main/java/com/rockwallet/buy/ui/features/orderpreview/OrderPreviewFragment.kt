package com.rockwallet.buy.ui.features.orderpreview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.util.formatFiatForUi
import com.breadwallet.util.formatFiatForUiAdvanced
import com.rockwallet.buy.R
import com.rockwallet.buy.databinding.FragmentOrderPreviewBinding
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.ui.dialog.InfoDialog
import com.rockwallet.common.ui.dialog.InfoDialogArgs
import com.rockwallet.common.utils.RockWalletToastUtil
import com.rockwallet.common.utils.formatPercent
import com.rockwallet.common.utils.textOrEmpty
import com.rockwallet.common.utils.underline
import com.rockwallet.trade.ui.features.authentication.SwapAuthenticationViewModel
import kotlinx.coroutines.flow.collect

class OrderPreviewFragment : Fragment(),
    RockWalletView<OrderPreviewContract.State, OrderPreviewContract.Effect> {

    private lateinit var binding: FragmentOrderPreviewBinding
    private val viewModel: OrderPreviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_order_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrderPreviewBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnBackPressed)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnDismissClicked)
            }

            ivInfoCredit.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnCreditInfoClicked)
            }

            ivInfoNetwork.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnNetworkInfoClicked)
            }

            ivInfoSecurityCode.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnSecurityCodeInfoClicked)
            }

            etCvv.addTextChangedListener {
                viewModel.setEvent(
                    OrderPreviewContract.Event.OnSecurityCodeChanged(
                        it.textOrEmpty()
                    )
                )
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnConfirmClicked)
            }

            tvTermsConditionsLink.underline()
            tvTermsConditionsLink.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnTermsAndConditionsClicked)
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

        // listen for user authentication result
        parentFragmentManager.setFragmentResultListener(SwapAuthenticationViewModel.REQUEST_KEY, this) { _, bundle ->
            val resultKey = bundle.getString(SwapAuthenticationViewModel.EXTRA_RESULT)
            if (resultKey == SwapAuthenticationViewModel.RESULT_KEY_SUCCESS) {
                binding.root.post {
                    viewModel.setEvent(OrderPreviewContract.Event.OnUserAuthenticationSucceed)
                }
            }
        }
    }

    override fun render(state: OrderPreviewContract.State) {
        with(binding) {
            ivCrypto.postLoadIcon(state.cryptoCurrency)
            btnConfirm.isEnabled = state.confirmButtonEnabled

            tvTotalAmount.text = state.totalFiatAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true
            )

            tvAmountValue.text = state.fiatAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true
            )

            tvCryptoAmount.text = state.cryptoAmount.formatCryptoForUi(state.cryptoCurrency, 8)

            tvCreditFeeValue.text = state.cardFee.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true
            )

            tvAchFeeValue.text = state.achFee.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true
            )

            tvCreditFeeTitle.text = "${getString(R.string.Swap_CardFee)} (${state.cardFeePercent.formatPercent()})"

            tvNetworkFeeValue.text = state.networkFee.formatFiatForUi()

            tvRateValue.text = RATE_FORMAT.format(
                state.cryptoCurrency,
                state.oneCryptoUnitToFiatRate.formatFiatForUiAdvanced(
                    currencyCode = state.fiatCurrency,
                    showCurrencyName = true
                )
            )

            fullScreenLoadingView.root.isVisible = state.fullScreenLoadingIndicator

            cbTerms.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setEvent(
                    OrderPreviewContract.Event.OnTermsChanged(isChecked)
                )
            }

            when (state.paymentInstrument) {
                is PaymentInstrument.Card -> {
                    tvAchWarning.isVisible = false
                    groupAchFee.isVisible = false
                    groupCardFee.isVisible = true
                    viewAchPayment.isVisible = false
                    cvPaymentMethod.isVisible = true

                    viewCreditCard.setPaymentInstrument(state.paymentInstrument)
                }
                is PaymentInstrument.BankAccount -> {
                    tvAchWarning.isVisible = true
                    groupAchFee.isVisible = true
                    groupCardFee.isVisible = false
                    viewCreditCard.isVisible = false
                    cvPaymentMethod.isVisible = false

                    val achFixedFee = state.achFixedFeeUsd.formatFiatForUi(
                        currencyCode = state.fiatCurrency,
                        showCurrencyName = false
                    )

                    val achFeePercent = state.achFeePercent.formatPercent()

                    tvAchFeeTitle.text = getString(
                        R.string.Buy_achFee, "$achFixedFee + $achFeePercent"
                    )

                    viewAchPayment.setPaymentInstrument(state.paymentInstrument)
                }
            }
        }
    }

    override fun handleEffect(effect: OrderPreviewContract.Effect) {
        when (effect) {
            OrderPreviewContract.Effect.Back ->
                findNavController().popBackStack()

            OrderPreviewContract.Effect.Dismiss ->
                activity?.finish()

            OrderPreviewContract.Effect.TimeoutScreen ->
                findNavController().navigate(
                    OrderPreviewFragmentDirections.actionPaymentTimeout()
                )

            is OrderPreviewContract.Effect.PaymentProcessing -> {
                // set callback
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to RESULT_NEW_QUOTE))

                findNavController().navigate(
                    OrderPreviewFragmentDirections.actionPaymentProcessing(
                        redirectUrl = effect.redirectUrl,
                        paymentType = effect.paymentType,
                        paymentReference = effect.paymentReference
                    )
                )
            }

            OrderPreviewContract.Effect.RequestUserAuthentication ->
                findNavController().navigate(
                    OrderPreviewFragmentDirections.actionUserAuthentication()
                )

            is OrderPreviewContract.Effect.ShowError ->
                RockWalletToastUtil.showError(binding.root, effect.message)

            is OrderPreviewContract.Effect.ShowInfoDialog ->
                showInfoDialog(effect)

            is OrderPreviewContract.Effect.OpenWebsite -> {
                val uri = Uri.parse(effect.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }

    private fun showInfoDialog(effect: OrderPreviewContract.Effect.ShowInfoDialog) {
        val args = InfoDialogArgs(
            image = effect.image,
            title = effect.title,
            description = effect.description
        )

        InfoDialog(args).show(parentFragmentManager, InfoDialog.TAG)
    }
    
    companion object {
        private const val RATE_FORMAT = "1 %s = %s"
        const val REQUEST_KEY = "request_order_preview"
        const val RESULT_KEY = "result_order_preview"
        const val RESULT_NEW_QUOTE = "quote_used"
    }
}