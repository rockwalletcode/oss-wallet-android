package com.rockwallet.buy.ui.features.timeout

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface PaymentTimeoutEventHandler: RockWalletEventHandler<PaymentTimeoutContract.Event> {

    override fun handleEvent(event: PaymentTimeoutContract.Event) {
        return when (event) {
            is PaymentTimeoutContract.Event.TryAgainClicked -> onTryAgainClicked()
        }
    }

    fun onTryAgainClicked()
}