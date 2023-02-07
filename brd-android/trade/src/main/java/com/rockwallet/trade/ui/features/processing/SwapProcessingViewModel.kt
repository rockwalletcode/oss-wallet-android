package com.rockwallet.trade.ui.features.processing

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.toBundle

class SwapProcessingViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<SwapProcessingContract.State, SwapProcessingContract.Event, SwapProcessingContract.Effect>(
    application, savedStateHandle
), SwapProcessingEventHandler {
    private lateinit var arguments: SwapProcessingFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = SwapProcessingFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = SwapProcessingContract.State(
        originCurrency = arguments.coinFrom,
        destinationCurrency = arguments.coinTo
    )

    override fun onDismissClicked() {
        setEffect { SwapProcessingContract.Effect.Dismiss }
    }

    override fun onGoHomeClicked() {
        setEffect { SwapProcessingContract.Effect.GoHome }
    }

    override fun onOpenSwapDetailsClicked() {
        setEffect { SwapProcessingContract.Effect.OpenDetails(arguments.exchangeId) }
    }
}