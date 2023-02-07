package com.rockwallet.registration.ui.features.enteremail

import com.rockwallet.common.ui.base.RockWalletContract

interface RegistrationEnterEmailContract {

    sealed class Event : RockWalletContract.Event {
        object NextClicked : Event()
        object DismissClicked : Event()
        data class PromotionsClicked(val checked: Boolean) : Event()
        data class EmailChanged(val email: String) : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String) : Effect()
        data class GoToVerifyEmail(val email: String) : Effect()
    }

    data class State(
        val email: String = "",
        val nextEnabled: Boolean = false,
        val promotionsEnabled: Boolean = true,
        val loadingVisible: Boolean = false
    ): RockWalletContract.State
}