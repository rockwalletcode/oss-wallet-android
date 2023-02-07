package com.rockwallet.trade.ui.features.failed

import android.app.Application
import com.rockwallet.common.ui.base.RockWalletViewModel

class SwapFailedViewModel(
    application: Application
) : RockWalletViewModel<SwapFailedContract.State, SwapFailedContract.Event, SwapFailedContract.Effect>(
    application
), SwapFailedEventHandler {

    override fun createInitialState() = SwapFailedContract.State()

    override fun onGoHomeClicked() {
        setEffect { SwapFailedContract.Effect.Dismiss }
    }

    override fun onSwapAgainClicked() {
        setEffect { SwapFailedContract.Effect.Back }
    }
}