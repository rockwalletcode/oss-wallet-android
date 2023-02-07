package com.rockwallet.buy.ui.features.timeout

import com.rockwallet.common.ui.base.RockWalletContract

class PaymentTimeoutContract : RockWalletContract {

    sealed class Event : RockWalletContract.Event {
        object TryAgainClicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object BackToBuy : Effect()
    }

    object State : RockWalletContract.State
}