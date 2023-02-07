package com.rockwallet.buy.ui.features.paymentmethod

import com.rockwallet.buy.ui.features.addcard.AddCardFlow
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.common.ui.dialog.RockWalletGenericDialogArgs

interface PaymentMethodContract : RockWalletContract {

    sealed class Event : RockWalletContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object AddCardClicked : Event()
        data class RemoveOptionClicked(val paymentInstrument: PaymentInstrument): Event()
        data class PaymentInstrumentClicked(val paymentInstrument: PaymentInstrument): Event()
        data class PaymentInstrumentOptionsClicked(val paymentInstrument: PaymentInstrument): Event()
        data class PaymentInstrumentRemovalConfirmed(val paymentInstrument: PaymentInstrument): Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Dismiss : Effect()
        data class AddCard(val flow: AddCardFlow) : Effect()
        data class Back(val result: PaymentMethodFragment.Result) : Effect()
        data class ShowError(val message: String) : Effect()
        data class ShowToast(val message: String): Effect()
        data class ShowOptionsBottomSheet(val paymentInstrument: PaymentInstrument) : Effect()
        data class ShowConfirmationDialog(val args: RockWalletGenericDialogArgs) : Effect()
    }

    data class State(
        val dataUpdated: Boolean = false,
        val paymentInstruments: List<PaymentInstrument.Card>,
        val initialLoadingIndicator: Boolean = false,
        val fullScreenLoadingIndicator: Boolean = false,
        val showDismissButton: Boolean = false
    ) : RockWalletContract.State
}