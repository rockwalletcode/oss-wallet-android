package com.rockwallet.trade.ui.features.processing

import com.rockwallet.common.ui.base.RockWalletContract

interface SwapProcessingContract {

    sealed class Event : RockWalletContract.Event {
        object DismissClicked : Event()
        object GoHomeClicked : Event()
        object OpenSwapDetails : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Dismiss : Effect()
        object GoHome : Effect()
        data class OpenDetails(val id: String) : Effect()
    }

    data class State(
        val originCurrency: String,
        val destinationCurrency: String
    ) : RockWalletContract.State
}