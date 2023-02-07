package com.rockwallet.trade.ui.features.failed

import com.rockwallet.common.ui.base.RockWalletContract

interface SwapFailedContract {

    sealed class Event : RockWalletContract.Event {
        object GoHomeClicked : Event()
        object SwapAgainClicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
    }

    class State : RockWalletContract.State
}