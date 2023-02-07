package com.rockwallet.kyc.ui.features.countryselection

import com.rockwallet.common.ui.base.RockWalletEventHandler
import com.rockwallet.kyc.data.model.Country

interface CountrySelectionEventHandler: RockWalletEventHandler<CountrySelectionContract.Event> {

    override fun handleEvent(event: CountrySelectionContract.Event) {
        return when (event) {
            is CountrySelectionContract.Event.BackClicked -> onBackClicked()
            is CountrySelectionContract.Event.SearchChanged -> onSearchChanged(event.query)
            is CountrySelectionContract.Event.LoadCountries -> onLoadCountries()
            is CountrySelectionContract.Event.CountrySelected -> onCountrySelected(event.country)
        }
    }

    fun onBackClicked()

    fun onLoadCountries()

    fun onSearchChanged(query: String?)

    fun onCountrySelected(country: Country)
}