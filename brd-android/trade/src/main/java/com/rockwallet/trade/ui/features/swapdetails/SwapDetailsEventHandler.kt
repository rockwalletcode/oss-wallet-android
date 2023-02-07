package com.rockwallet.trade.ui.features.swapdetails

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface SwapDetailsEventHandler: RockWalletEventHandler<SwapDetailsContract.Event> {

    override fun handleEvent(event: SwapDetailsContract.Event) {
        return when (event) {
            is SwapDetailsContract.Event.LoadData -> onLoadData()
            is SwapDetailsContract.Event.DismissClicked -> onDismissClicked()
            is SwapDetailsContract.Event.OrderIdClicked -> onOrderIdClicked()
            is SwapDetailsContract.Event.SourceTransactionIdClicked -> onSourceTransactionIdClicked()
            is SwapDetailsContract.Event.DestinationTransactionIdClicked -> onDestinationTransactionIdClicked()
        }
    }

    fun onLoadData()

    fun onDismissClicked()

    fun onOrderIdClicked()

    fun onSourceTransactionIdClicked()

    fun onDestinationTransactionIdClicked()
}