package com.rockwallet.buy.ui.features.plaiderror

import android.app.Application
import com.rockwallet.common.ui.base.RockWalletViewModel

class PlaidConnectionErrorViewModel(
    application: Application
) : RockWalletViewModel<PlaidConnectionErrorContract.State, PlaidConnectionErrorContract.Event, PlaidConnectionErrorContract.Effect>(
    application
), PlaidConnectionErrorEventHandler {

    override fun createInitialState() = PlaidConnectionErrorContract.State

    override fun onTryAgainClicked() {
        setEffect { PlaidConnectionErrorContract.Effect.BackToBuy }
    }

    override fun onContactSupportClicked() {
        setEffect { PlaidConnectionErrorContract.Effect.ContactSupport }
    }
}