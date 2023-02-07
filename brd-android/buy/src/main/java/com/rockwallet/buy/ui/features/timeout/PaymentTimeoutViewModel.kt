package com.rockwallet.buy.ui.features.timeout

import android.app.Application
import com.rockwallet.common.ui.base.RockWalletViewModel

class PaymentTimeoutViewModel(
    application: Application
) : RockWalletViewModel<PaymentTimeoutContract.State, PaymentTimeoutContract.Event, PaymentTimeoutContract.Effect>(
    application
), PaymentTimeoutEventHandler {

    override fun createInitialState() = PaymentTimeoutContract.State

    override fun onTryAgainClicked() {
        setEffect { PaymentTimeoutContract.Effect.BackToBuy }
    }
}