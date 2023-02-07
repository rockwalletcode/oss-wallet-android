package com.rockwallet.kyc.ui.features.prevalidation

import com.rockwallet.common.ui.base.RockWalletContract

interface PreValidationContract {

    sealed class Event : RockWalletContract.Event{
        object BackClicked : Event()
        object ConfirmClicked: Event()
        object DismissCLicked: Event()
    }

    sealed class Effect : RockWalletContract.Effect{
        object Back : Effect()
        object ProofOfIdentity : Effect()
        object Dismiss : Effect()
    }

    object State : RockWalletContract.State
}