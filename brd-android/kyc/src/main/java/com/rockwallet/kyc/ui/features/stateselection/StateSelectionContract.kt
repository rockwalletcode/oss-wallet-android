package com.rockwallet.kyc.ui.features.stateselection

import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.kyc.data.model.CountryState

interface StateSelectionContract {

    sealed class Event : RockWalletContract.Event {
        object BackClicked : Event()
        data class SearchChanged(val query: String?) : Event()
        data class StateSelected(val state: CountryState) : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        data class ShowToast(val message: String): Effect()
        data class Back(
            val requestKey: String,
            val selectedState: CountryState?
        ) : Effect()
    }

    data class State(
        val search: String = "",
        val states: List<CountryState>,
        val adapterItems: List<CountryState>
    ) : RockWalletContract.State
}