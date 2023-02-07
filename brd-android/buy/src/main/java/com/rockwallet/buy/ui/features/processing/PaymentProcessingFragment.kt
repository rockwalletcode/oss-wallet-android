package com.rockwallet.buy.ui.features.processing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rockwallet.buy.R
import com.rockwallet.buy.data.enums.BuyDetailsFlow
import com.rockwallet.buy.databinding.FragmentPaymentProcessingBinding
import com.rockwallet.common.data.RockWalletApiConstants
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.RockWalletToastUtil
import com.rockwallet.common.utils.underline
import kotlinx.coroutines.flow.collect

class PaymentProcessingFragment : Fragment(),
    RockWalletView<PaymentProcessingContract.State, PaymentProcessingContract.Effect> {

    private lateinit var binding: FragmentPaymentProcessingBinding
    private val viewModel: PaymentProcessingViewModel by viewModels()

    private val paymentRedirectResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.setEvent(PaymentProcessingContract.Event.OnPaymentRedirectResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_payment_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentProcessingBinding.bind(view)

        with(binding) {
            btnHome.setOnClickListener {
                viewModel.setEvent(PaymentProcessingContract.Event.BackToHomeClicked)
            }

            btnContactSupport.setOnClickListener {
                viewModel.setEvent(PaymentProcessingContract.Event.ContactSupportClicked)
            }

            btnPurchaseDetails.setOnClickListener {
                viewModel.setEvent(PaymentProcessingContract.Event.PurchaseDetailsClicked)
            }

            btnDifferentMethod.setOnClickListener {
                viewModel.setEvent(PaymentProcessingContract.Event.TryDifferentMethodClicked)
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
    }

    override fun render(state: PaymentProcessingContract.State) {
        when (state) {
            is PaymentProcessingContract.State.Processing ->
                showProcessingScreen(state)

            is PaymentProcessingContract.State.PaymentFailed ->
                showPaymentFailedScreen(state)

            is PaymentProcessingContract.State.PaymentCompleted ->
                showPaymentCompletedScreen(state)
        }
    }

    private fun showProcessingScreen(state: PaymentProcessingContract.State.Processing) {
        with(binding) {
            contentLoaded.isVisible = false
            contentProcessing.isVisible = true
            tvProcessingTitle.setText(state.title)
        }
    }

    private fun showPaymentFailedScreen(state: PaymentProcessingContract.State.PaymentFailed) {
        with(binding) {
            contentLoaded.isVisible = true
            contentProcessing.isVisible = false

            ivIcon.setAnimation(R.raw.lottie_error)
            tvTitle.setText(state.title)
            tvDescription.setText(state.description)

            btnHome.isVisible = false
            btnContactSupport.isVisible = true
            btnPurchaseDetails.isVisible = false
            btnDifferentMethod.isVisible = true

            btnContactSupport.underline()
        }
    }

    private fun showPaymentCompletedScreen(state: PaymentProcessingContract.State.PaymentCompleted) {
        with(binding) {
            contentLoaded.isVisible = true
            contentProcessing.isVisible = false

            ivIcon.setAnimation(R.raw.lottie_completed)
            tvTitle.setText(state.title)
            tvDescription.setText(state.description)

            btnHome.isVisible = true
            btnContactSupport.isVisible = false
            btnPurchaseDetails.isVisible = true
            btnDifferentMethod.isVisible = false

            btnPurchaseDetails.underline()
        }
    }

    override fun handleEffect(effect: PaymentProcessingContract.Effect) {
        when (effect) {
            PaymentProcessingContract.Effect.Dismiss ->
                activity?.finish()

            PaymentProcessingContract.Effect.BackToBuy ->
                findNavController().popBackStack(
                    R.id.fragmentBuyInput, false
                )

            PaymentProcessingContract.Effect.ContactSupport -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(RockWalletApiConstants.URL_SUPPORT_PAGE))
                startActivity(intent)
            }

            is PaymentProcessingContract.Effect.GoToPurchaseDetails ->
                findNavController().navigate(
                    PaymentProcessingFragmentDirections.actionBuyDetails(
                        exchangeId = effect.purchaseId,
                        flow = BuyDetailsFlow.PURCHASE
                    )
                )

            is PaymentProcessingContract.Effect.OpenPaymentRedirect -> {
                val uri = Uri.parse(effect.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                paymentRedirectResultLauncher.launch(intent)
            }

            is PaymentProcessingContract.Effect.ShowError ->
                RockWalletToastUtil.showError(binding.root, effect.message)
        }
    }
}