package com.rockwallet.kyc.ui.features.accountverification

import com.rockwallet.common.data.model.Profile
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.kyc.ui.customview.AccountVerificationStatusView

interface AccountVerificationContract {

    sealed class Event : RockWalletContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object InfoClicked : Event()
        object Level1Clicked : Event()
        object Level2Clicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Back : Effect()
        object Info : Effect()
        object Dismiss : Effect()
        object GoToKycLevel1 : Effect()
        object GoToKycLevel2 : Effect()
        object ShowLevel1ChangeConfirmationDialog : Effect()
        data class ShowToast(val message: String) : Effect()
    }

    sealed class State : RockWalletContract.State {

        data class Empty(
            val isLoading: Boolean = true
        ) : State()

        data class Content(
            val profile: Profile,
            val level1State: Level1State,
            val level2State: Level2State,
        ) : State()
    }

    data class Level1State(
        val isEnabled: Boolean = false,
        val statusState: AccountVerificationStatusView.StatusViewState? = null,
    ) : RockWalletContract.State

    data class Level2State(
        val isEnabled: Boolean = false,
        val statusState: AccountVerificationStatusView.StatusViewState? = null,
        val verificationError: String? = null,
    ) : RockWalletContract.State
}