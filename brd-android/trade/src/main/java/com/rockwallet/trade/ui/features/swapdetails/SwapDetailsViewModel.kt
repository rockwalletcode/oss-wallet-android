package com.rockwallet.trade.ui.features.swapdetails

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.rockwallet.common.data.Status
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.getString
import com.rockwallet.common.utils.toBundle
import com.rockwallet.trade.R
import com.rockwallet.trade.data.SwapApi
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class SwapDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) :
    RockWalletViewModel<SwapDetailsContract.State, SwapDetailsContract.Event, SwapDetailsContract.Effect>(
        application, savedStateHandle
    ), SwapDetailsEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val swapApi by kodein.instance<SwapApi>()

    private lateinit var arguments: SwapDetailsFragmentArgs

    private val currentLoadedState: SwapDetailsContract.State.Loaded?
        get() = state.value as SwapDetailsContract.State.Loaded?

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = SwapDetailsFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = SwapDetailsContract.State.Loading

    override fun onLoadData() {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { swapApi.getExchangeOrder(arguments.exchangeId) },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setState { SwapDetailsContract.State.Loaded(requireNotNull(it.data)) }

                    Status.ERROR -> {
                        setState { SwapDetailsContract.State.Error }
                        setEffect {
                            SwapDetailsContract.Effect.ShowToast(
                                it.message ?: getString(R.string.ErrorMessages_default)
                            )
                        }
                    }
                }
            }
        )
    }

    override fun onDismissClicked() {
        setEffect { SwapDetailsContract.Effect.Dismiss }
    }

    override fun onOrderIdClicked() {
        val state = currentLoadedState ?: return
        setEffect { SwapDetailsContract.Effect.CopyToClipboard(state.data.orderId) }
    }

    override fun onSourceTransactionIdClicked() {
        val transactionID = currentLoadedState?.data?.source?.transactionId ?: return
        setEffect { SwapDetailsContract.Effect.CopyToClipboard(transactionID) }
    }

    override fun onDestinationTransactionIdClicked() {
        val transactionID = currentLoadedState?.data?.destination?.transactionId ?: return
        setEffect { SwapDetailsContract.Effect.CopyToClipboard(transactionID) }
    }
}