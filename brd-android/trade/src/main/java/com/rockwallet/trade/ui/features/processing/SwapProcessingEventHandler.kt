package com.rockwallet.trade.ui.features.processing

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface SwapProcessingEventHandler: RockWalletEventHandler<SwapProcessingContract.Event> {

    override fun handleEvent(event: SwapProcessingContract.Event) {
        return when (event) {
            is SwapProcessingContract.Event.GoHomeClicked -> onGoHomeClicked()
            is SwapProcessingContract.Event.DismissClicked -> onDismissClicked()
            is SwapProcessingContract.Event.OpenSwapDetails -> onOpenSwapDetailsClicked()
        }
    }

    fun onDismissClicked()

    fun onGoHomeClicked()

    fun onOpenSwapDetailsClicked()
}