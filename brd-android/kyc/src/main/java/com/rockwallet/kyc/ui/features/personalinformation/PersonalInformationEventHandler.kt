package com.rockwallet.kyc.ui.features.personalinformation

import com.rockwallet.common.ui.base.RockWalletEventHandler
import com.rockwallet.kyc.data.model.Country
import com.rockwallet.kyc.data.model.CountryState

interface PersonalInformationEventHandler: RockWalletEventHandler<PersonalInformationContract.Event> {

    override fun handleEvent(event: PersonalInformationContract.Event) {
        return when (event) {
            is PersonalInformationContract.Event.LoadProfile -> onLoadProfile()
            is PersonalInformationContract.Event.BackClicked -> onBackClicked()
            is PersonalInformationContract.Event.DateClicked -> onDateClicked()
            is PersonalInformationContract.Event.StateClicked -> onStateClicked()
            is PersonalInformationContract.Event.CountryClicked -> onCountryClicked()
            is PersonalInformationContract.Event.ConfirmClicked -> onConfirmClicked()
            is PersonalInformationContract.Event.DismissClicked -> onDismissClicked()
            is PersonalInformationContract.Event.NameChanged -> onNameChanged(event.name)
            is PersonalInformationContract.Event.DateChanged -> onDateChanged(event.date)
            is PersonalInformationContract.Event.LastNameChanged -> onLastNameChanged(event.lastName)
            is PersonalInformationContract.Event.StateChanged -> onStateChanged(event.state)
            is PersonalInformationContract.Event.CountryChanged -> onCountryChanged(event.country)
        }
    }

    fun onBackClicked()

    fun onDateClicked()

    fun onStateClicked()

    fun onCountryClicked()

    fun onConfirmClicked()

    fun onDismissClicked()

    fun onLoadProfile()

    fun onNameChanged(name: String)

    fun onDateChanged(date: Long)

    fun onLastNameChanged(lastName: String)

    fun onStateChanged(state: CountryState)

    fun onCountryChanged(country: Country)
}