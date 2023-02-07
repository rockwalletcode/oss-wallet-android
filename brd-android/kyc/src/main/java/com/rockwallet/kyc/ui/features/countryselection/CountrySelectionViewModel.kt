package com.rockwallet.kyc.ui.features.countryselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.rockwallet.common.data.Status
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.FlagUtil
import com.rockwallet.common.utils.getString
import com.rockwallet.common.utils.toBundle
import com.rockwallet.kyc.R
import com.rockwallet.kyc.data.KycApi
import com.rockwallet.kyc.data.model.Country

class CountrySelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<CountrySelectionContract.State, CountrySelectionContract.Event, CountrySelectionContract.Effect>(
    application, savedStateHandle
), CountrySelectionEventHandler {

    private lateinit var arguments: CountrySelectionFragmentArgs

    private val kycApi = KycApi.create(application.applicationContext)

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = CountrySelectionFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = CountrySelectionContract.State()

    override fun onBackClicked() {
        setEffect {
            CountrySelectionContract.Effect.Back(
                requestKey = arguments.requestKey,
                selectedCountry = null
            )
        }
    }

    override fun onCountrySelected(country: Country) {
        setEffect {
            CountrySelectionContract.Effect.Back(
                requestKey = arguments.requestKey,
                selectedCountry = country
            )
        }
    }

    override fun onSearchChanged(query: String?) {
        setState { copy(search = query ?: "") }
        applyFilters()
    }

    override fun onLoadCountries() {
        callApi(
            endState = { copy(initialLoadingVisible = false) },
            startState = { copy(initialLoadingVisible = true) },
            action = { kycApi.getCountries() },
            callback = { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        setState {
                            val us = response.data!!.find { it.code == "US" }
                            val arrayList = ArrayList(response.data!!)
                            arrayList.add(0, us)
                            copy(countries = arrayList)
                        }
                        initialState()
                    }

                    Status.ERROR ->
                        setEffect {
                            CountrySelectionContract.Effect.ShowToast(
                                response.message ?: getString(R.string.ErrorMessages_default)
                            )
                        }
                }
            }
        )
    }

    private fun initialState() {
        setState {
            copy(
                adapterItems = currentState.countries.map {
                    CountrySelectionAdapter.Item(
                        icon = FlagUtil.getDrawableId(getApplication(), it.code),
                        country = it
                    )
                }
            )
        }
    }

    private fun applyFilters() {
        setState {
            copy(
                adapterItems = currentState.countries.filter {
                    it.name.contains(
                        other = currentState.search,
                        ignoreCase = true
                    )
                }.distinct().map {
                    CountrySelectionAdapter.Item(
                        icon = FlagUtil.getDrawableId(getApplication(), it.code),
                        country = it
                    )
                }
            )
        }
    }
}