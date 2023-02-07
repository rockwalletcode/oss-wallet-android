package com.rockwallet.kyc.ui.features.postvalidation

import com.rockwallet.common.ui.base.RockWalletContract

interface PostValidationContract {

    sealed class Event : RockWalletContract.Event {
        object ConfirmClicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Profile : Effect()
    }

    object State : RockWalletContract.State
}