package com.rockwallet.trade.ui.features.failed

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface SwapFailedEventHandler: RockWalletEventHandler<SwapFailedContract.Event> {

    override fun handleEvent(event: SwapFailedContract.Event) {
        return when (event) {
            is SwapFailedContract.Event.GoHomeClicked -> onGoHomeClicked()
            is SwapFailedContract.Event.SwapAgainClicked -> onSwapAgainClicked()
        }
    }

    fun onGoHomeClicked()

    fun onSwapAgainClicked()
}