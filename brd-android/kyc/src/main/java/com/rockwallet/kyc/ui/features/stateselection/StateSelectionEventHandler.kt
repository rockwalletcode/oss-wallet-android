package com.rockwallet.kyc.ui.features.stateselection

import com.rockwallet.common.ui.base.RockWalletEventHandler
import com.rockwallet.kyc.data.model.CountryState

interface StateSelectionEventHandler: RockWalletEventHandler<StateSelectionContract.Event> {

    override fun handleEvent(event: StateSelectionContract.Event) {
        return when (event) {
            is StateSelectionContract.Event.BackClicked -> onBackClicked()
            is StateSelectionContract.Event.SearchChanged -> onSearchChanged(event.query)
            is StateSelectionContract.Event.StateSelected -> onStateSelected(event.state)
        }
    }

    fun onBackClicked()

    fun onSearchChanged(query: String?)

    fun onStateSelected(state: CountryState)
}