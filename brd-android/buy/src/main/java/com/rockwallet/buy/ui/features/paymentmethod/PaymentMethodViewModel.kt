package com.rockwallet.buy.ui.features.paymentmethod

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import com.rockwallet.buy.R
import com.rockwallet.buy.data.BuyApi
import com.rockwallet.buy.ui.features.addcard.AddCardFlow
import com.rockwallet.common.data.Status
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.ui.dialog.RockWalletGenericDialogArgs
import com.rockwallet.common.utils.getString
import com.rockwallet.common.utils.toBundle
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class PaymentMethodViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<PaymentMethodContract.State, PaymentMethodContract.Event, PaymentMethodContract.Effect>(
    application, savedStateHandle
), PaymentMethodEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    private lateinit var arguments: PaymentMethodFragmentArgs

    init {
        loadInitialData()
    }

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        super.parseArguments(savedStateHandle)

        arguments = PaymentMethodFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = PaymentMethodContract.State(
        paymentInstruments = emptyList(),
        showDismissButton = arguments.flow == AddCardFlow.BUY
    )

    override fun onBackClicked() {
        setEffect {
            if (arguments.flow == AddCardFlow.BUY) {
                PaymentMethodContract.Effect.Back(
                    PaymentMethodFragment.Result.Cancelled(currentState.dataUpdated)
                )
            } else {
                PaymentMethodContract.Effect.Dismiss
            }
        }
    }

    override fun onDismissClicked() {
        setEffect { PaymentMethodContract.Effect.Dismiss }
    }

    override fun onAddCardClicked() {
        setEffect { PaymentMethodContract.Effect.AddCard(arguments.flow) }
    }

    override fun onRemoveOptionClicked(paymentInstrument: PaymentInstrument) {
        if (paymentInstrument !is PaymentInstrument.Card) return

        setEffect {
            PaymentMethodContract.Effect.ShowConfirmationDialog(
                RockWalletGenericDialogArgs(
                    requestKey = REQUEST_CONFIRMATION_DIALOG,
                    title = getString(R.string.Buy_RemoveCard_android, paymentInstrument.last4Numbers),
                    positive = RockWalletGenericDialogArgs.ButtonData(
                        titleRes = R.string.Button_remove,
                        resultKey = RESULT_CONFIRMATION_DIALOG_REMOVE
                    ),
                    negative = RockWalletGenericDialogArgs.ButtonData(
                        titleRes = R.string.Button_cancel,
                        resultKey = RESULT_CONFIRMATION_DIALOG_CANCEL
                    ),
                    extraData = bundleOf(EXTRA_CONFIRMATION_DIALOG_DATA to paymentInstrument),
                    showDismissButton = true
                )
            )
        }
    }

    override fun onPaymentInstrumentClicked(paymentInstrument: PaymentInstrument) {
        if (arguments.flow == AddCardFlow.BUY) {
            setEffect {
                PaymentMethodContract.Effect.Back(
                    PaymentMethodFragment.Result.Selected(paymentInstrument)
                )
            }
        }
    }

    override fun onPaymentInstrumentOptionsClicked(paymentInstrument: PaymentInstrument) {
        setEffect { PaymentMethodContract.Effect.ShowOptionsBottomSheet(paymentInstrument) }
    }

    override fun onPaymentInstrumentRemovalConfirmed(paymentInstrument: PaymentInstrument) {
        if (paymentInstrument !is PaymentInstrument.Card) return

        callApi(
            endState = { copy(fullScreenLoadingIndicator = false) },
            startState = { copy(fullScreenLoadingIndicator = true) },
            action = { buyApi.deletePaymentInstrument(paymentInstrument) },
            callback = {
                val updatedList = currentState.paymentInstruments.toMutableList().apply {
                    remove(paymentInstrument)
                }

                setState {
                    copy(
                        paymentInstruments = updatedList,
                        dataUpdated = true
                    )
                }

                setEffect {
                    if (it.status == Status.SUCCESS) {
                        PaymentMethodContract.Effect.ShowToast(
                            getString(R.string.Buy_CardRemoved)
                        )
                    } else {
                        PaymentMethodContract.Effect.ShowError(
                            getString(R.string.Buy_CardRemovalFailed)
                        )
                    }
                }
            }
        )
    }

    private fun loadInitialData() {
        callApi(
            endState = { copy(initialLoadingIndicator = false) },
            startState = { copy(initialLoadingIndicator = true) },
            action = { buyApi.getPaymentInstruments() },
            callback = { response ->
                when (response.status) {
                    Status.SUCCESS ->
                        setState {
                            copy(
                                paymentInstruments = requireNotNull(response.data)
                                    .filterIsInstance<PaymentInstrument.Card>()
                            )
                        }

                    Status.ERROR ->
                        setEffect {
                            PaymentMethodContract.Effect.ShowError(
                                response.message ?: getString(R.string.ErrorMessages_default)
                            )
                        }
                }
            }
        )
    }

    companion object {
        const val EXTRA_CONFIRMATION_DIALOG_DATA = "extra_confirmation_dialog_data"
        const val REQUEST_CONFIRMATION_DIALOG = "request_confirmation_dialog"
        const val RESULT_CONFIRMATION_DIALOG_REMOVE = "result_confirmation_dialog_remove"
        const val RESULT_CONFIRMATION_DIALOG_CANCEL = "result_confirmation_dialog_cancel"
    }
}