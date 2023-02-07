package com.rockwallet.registration.ui.features.verifyemail

import android.app.Activity
import com.rockwallet.common.ui.base.RockWalletContract

interface RegistrationVerifyEmailContract {

    sealed class Event : RockWalletContract.Event {
        object ConfirmClicked : Event()
        object DismissClicked : Event()
        object ResendEmailClicked : Event()
        object ChangeEmailClicked : Event()
        data class CodeChanged(val code: String) : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Back: Effect()
        data class Dismiss(val resultCode: Int = Activity.RESULT_CANCELED): Effect()
        data class ShowToast(val message: String) : Effect()
    }

    data class State(
        val code: String = "",
        val subtitle: CharSequence,
        val confirmEnabled: Boolean = false,
        val loadingVisible: Boolean = false,
        val codeErrorVisible: Boolean = false,
        val completedViewVisible: Boolean = false,
        val changeEmailButtonVisible: Boolean = false
    ): RockWalletContract.State
}