package com.rockwallet.buy.ui.features.plaiderror

import com.rockwallet.common.ui.base.RockWalletContract

class PlaidConnectionErrorContract : RockWalletContract {

    sealed class Event : RockWalletContract.Event {
        object TryAgainClicked : Event()
        object ContactSupportClicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object BackToBuy : Effect()
        object ContactSupport : Effect()
    }

    object State : RockWalletContract.State
}