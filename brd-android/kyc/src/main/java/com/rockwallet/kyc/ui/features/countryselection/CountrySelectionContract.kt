package com.rockwallet.kyc.ui.features.countryselection

import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.kyc.data.model.Country

interface CountrySelectionContract {

    sealed class Event : RockWalletContract.Event {
        object BackClicked : Event()
        object LoadCountries : Event()
        data class SearchChanged(val query: String?) : Event()
        data class CountrySelected(val country: Country) : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        data class ShowToast(val message: String): Effect()
        data class Back(
            val requestKey: String,
            val selectedCountry: Country?
        ) : Effect()
    }

    data class State(
        val search: String = "",
        val countries: List<Country> = emptyList(),
        val adapterItems: List<CountrySelectionAdapter.Item> = emptyList(),
        val initialLoadingVisible: Boolean = false
    ) : RockWalletContract.State
}