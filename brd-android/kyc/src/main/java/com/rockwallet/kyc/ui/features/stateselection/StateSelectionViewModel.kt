package com.rockwallet.kyc.ui.features.stateselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.toBundle
import com.rockwallet.kyc.data.model.CountryState

class StateSelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<StateSelectionContract.State, StateSelectionContract.Event, StateSelectionContract.Effect>(
    application, savedStateHandle
), StateSelectionEventHandler {

    private lateinit var arguments: StateSelectionFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = StateSelectionFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = StateSelectionContract.State(
        adapterItems = arguments.states.data.toList(),
        states = arguments.states.data.toList()
    )

    override fun onBackClicked() {
        setEffect {
            StateSelectionContract.Effect.Back(
                requestKey = arguments.requestKey,
                selectedState = null
            )
        }
    }

    override fun onStateSelected(state: CountryState) {
        setEffect {
            StateSelectionContract.Effect.Back(
                requestKey = arguments.requestKey,
                selectedState = state
            )
        }
    }

    override fun onSearchChanged(query: String?) {
        setState { copy(search = query ?: "") }
        applyFilters()
    }

    private fun applyFilters() {
        setState {
            copy(
                adapterItems = currentState.states.filter {
                    it.name.contains(
                        other = currentState.search,
                        ignoreCase = true
                    )
                }.distinct()
            )
        }
    }
}