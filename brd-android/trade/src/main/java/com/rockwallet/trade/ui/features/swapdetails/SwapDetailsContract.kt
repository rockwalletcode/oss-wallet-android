package com.rockwallet.trade.ui.features.swapdetails

import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.trade.data.response.ExchangeOrder

interface SwapDetailsContract {

    sealed class Event : RockWalletContract.Event {
        object LoadData : Event()
        object DismissClicked : Event()
        object OrderIdClicked : Event()
        object SourceTransactionIdClicked : Event()
        object DestinationTransactionIdClicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String): Effect()
        data class CopyToClipboard(val data: String) : Effect()
    }

    sealed class State : RockWalletContract.State {
        object Loading : State()
        object Error : State()
        data class Loaded(
            val data: ExchangeOrder
        ) : State()
    }
}