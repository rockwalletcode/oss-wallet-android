package com.rockwallet.buy.ui.features.processing

import com.rockwallet.buy.R
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.trade.data.response.ExchangeType

class PaymentProcessingContract : RockWalletContract {

    sealed class Event : RockWalletContract.Event {
        object BackToHomeClicked : Event()
        object ContactSupportClicked: Event()
        object PurchaseDetailsClicked: Event()
        object TryDifferentMethodClicked: Event()
        object OnPaymentRedirectResult: Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Dismiss : Effect()
        object BackToBuy : Effect()
        object ContactSupport : Effect()

        data class ShowError(val message: String) : Effect()
        data class OpenPaymentRedirect(val url: String) : Effect()
        data class GoToPurchaseDetails(val purchaseId: String) : Effect()
    }

    sealed class State: RockWalletContract.State {
        data class Processing(
            val paymentReference: String?,
            val paymentType: ExchangeType
        ): State() {

            val title: Int
                get() = when (paymentType) {
                    ExchangeType.BUY -> R.string.Buy_ProcessingPayment
                    ExchangeType.BUY_ACH -> R.string.Buy_ConfirmingPayment
                    else -> throw IllegalStateException("Only buy types are supported")
                }
        }

        data class PaymentFailed(
            val paymentReference: String?,
            val paymentType: ExchangeType,
            val plaidErrorMessage: Int? = null
        ): State() {

            val title: Int
                get() = when (paymentType) {
                    ExchangeType.BUY ->
                        R.string.Buy_ErrorProcessingPayment
                    ExchangeType.BUY_ACH ->
                        plaidErrorMessage?.let { R.string.Buy_TransactionError } ?: R.string.Buy_ErrorProcessingPayment
                    else ->
                        throw IllegalStateException("Only buy types are supported")
                }

            val description: Int
                get() = when (paymentType) {
                    ExchangeType.BUY ->
                        R.string.Buy_FailureTransactionMessage
                    ExchangeType.BUY_ACH ->
                        plaidErrorMessage ?: R.string.Buy_FailureTransactionMessageAch
                    else ->
                        throw IllegalStateException("Only buy types are supported")
                }
        }

        data class PaymentCompleted(
            val paymentReference: String,
            val paymentType: ExchangeType
        ): State() {

            val title: Int
                get() = when (paymentType) {
                    ExchangeType.BUY -> R.string.Buy_PurchaseSuccessTitle
                    ExchangeType.BUY_ACH -> R.string.Buy_PurchaseSuccessTitleAch
                    else -> throw IllegalStateException("Only buy types are supported")
                }

            val description: Int
                get() = when (paymentType) {
                    ExchangeType.BUY -> R.string.Buy_PurchaseSuccessText
                    ExchangeType.BUY_ACH -> R.string.Buy_PurchaseSuccessTextAch
                    else -> throw IllegalStateException("Only buy types are supported")
                }
        }
    }
}