package com.rockwallet.buy.ui.features.processing

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface PaymentProcessingEventHandler: RockWalletEventHandler<PaymentProcessingContract.Event> {

    override fun handleEvent(event: PaymentProcessingContract.Event) {
        return when (event) {
            is PaymentProcessingContract.Event.BackToHomeClicked -> onBackToHomeClicked()
            is PaymentProcessingContract.Event.ContactSupportClicked -> onContactSupportClicked()
            is PaymentProcessingContract.Event.PurchaseDetailsClicked -> onPurchaseDetailsClicked()
            is PaymentProcessingContract.Event.OnPaymentRedirectResult -> onPaymentRedirectResult()
            is PaymentProcessingContract.Event.TryDifferentMethodClicked -> onTryDifferentMethodClicked()
        }
    }

    fun onBackToHomeClicked()

    fun onContactSupportClicked()

    fun onPurchaseDetailsClicked()

    fun onTryDifferentMethodClicked()

    fun onPaymentRedirectResult()
}