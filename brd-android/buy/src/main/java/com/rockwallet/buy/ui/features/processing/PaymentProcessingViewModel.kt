package com.rockwallet.buy.ui.features.processing

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.breadwallet.tools.security.ProfileManager
import com.rockwallet.buy.data.BuyApi
import com.rockwallet.buy.data.enums.PaymentStatus
import com.rockwallet.buy.utils.PlaidResponseCodeMapper
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.toBundle
import com.rockwallet.trade.data.SwapTransactionsFetcher
import com.rockwallet.trade.data.response.ExchangeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class PaymentProcessingViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<PaymentProcessingContract.State, PaymentProcessingContract.Event, PaymentProcessingContract.Effect>(
    application, savedStateHandle
), PaymentProcessingEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()
    private val profileManager by kodein.instance<ProfileManager>()
    private val plaidErrorMapper by kodein.instance<PlaidResponseCodeMapper>()
    private val transactionsFetcher by kodein.instance<SwapTransactionsFetcher>()

    private val currentProcessingState: PaymentProcessingContract.State.Processing?
        get() = state.value as PaymentProcessingContract.State.Processing?

    private val currentCompletedState: PaymentProcessingContract.State.PaymentCompleted?
        get() = state.value as PaymentProcessingContract.State.PaymentCompleted?

    private lateinit var arguments: PaymentProcessingFragmentArgs

    init {
        handlePaymentFlow()
    }

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = PaymentProcessingFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = PaymentProcessingContract.State.Processing(
        paymentReference = arguments.paymentReference,
        paymentType = arguments.paymentType
    )

    override fun onBackToHomeClicked() {
        setEffect { PaymentProcessingContract.Effect.Dismiss }
    }

    override fun onContactSupportClicked() {
        setEffect { PaymentProcessingContract.Effect.ContactSupport }
    }

    override fun onPurchaseDetailsClicked() {
        val purchaseId = currentCompletedState?.paymentReference
        if (!purchaseId.isNullOrBlank()) {
            setEffect { PaymentProcessingContract.Effect.GoToPurchaseDetails(purchaseId) }
        }
    }

    override fun onTryDifferentMethodClicked() {
        setEffect { PaymentProcessingContract.Effect.BackToBuy }
    }

    override fun onPaymentRedirectResult() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(INITIAL_DELAY)
            checkPaymentStatus()
        }
    }

    private fun handlePaymentFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(INITIAL_DELAY)

            val redirectUrl = arguments.redirectUrl
            if (redirectUrl.isNullOrBlank()) {
                checkPaymentStatus()
            } else {
                setEffect { PaymentProcessingContract.Effect.OpenPaymentRedirect(redirectUrl) }
            }
        }
    }

    private fun checkPaymentStatus() {
        val reference = currentProcessingState?.paymentReference ?: return

        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { buyApi.getPaymentStatus(reference) },
            callback = {
                setState {
                    if (isPaymentPending(it.data?.status)) {
                        // update swap/buy transactions
                        transactionsFetcher.refreshData()

                        // update profile limits
                        profileManager.updateProfile()

                        PaymentProcessingContract.State.PaymentCompleted(
                            paymentReference = reference,
                            paymentType = arguments.paymentType
                        )
                    } else {
                        PaymentProcessingContract.State.PaymentFailed(
                            paymentReference = reference,
                            paymentType = arguments.paymentType,
                            plaidErrorMessage = it.data?.responseCode?.let { plaidErrorMapper(it) }
                        )
                    }
                }
            }
        )
    }

    private fun isPaymentPending(status: PaymentStatus?) = when(arguments.paymentType) {
        ExchangeType.BUY -> CARD_PENDING_STATUSES.contains(status)
        ExchangeType.BUY_ACH -> ACH_PENDING_STATUSES.contains(status)
        ExchangeType.SWAP -> throw IllegalStateException("Swap type not allowed in this flow")
    }

    companion object {
        private const val INITIAL_DELAY = 1000L

        private val ACH_PENDING_STATUSES = arrayOf(
            PaymentStatus.AUTHORIZED, PaymentStatus.PENDING
        )

        private val CARD_PENDING_STATUSES = arrayOf(
            PaymentStatus.CAPTURED, PaymentStatus.CARD_VERIFIED
        )
    }
}