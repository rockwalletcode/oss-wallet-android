package com.rockwallet.kyc.ui.features.personalinformation

import android.app.Activity
import com.rockwallet.common.data.model.Profile
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.kyc.data.model.Country
import com.rockwallet.kyc.data.model.CountryState
import java.util.*

interface PersonalInformationContract {

    sealed class Event : RockWalletContract.Event {
        object LoadProfile : Event()
        object BackClicked : Event()
        object DateClicked : Event()
        object StateClicked : Event()
        object CountryClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()

        class NameChanged(val name: String) : PersonalInformationContract.Event()
        class StateChanged(val state: CountryState) : PersonalInformationContract.Event()
        class CountryChanged(val country: Country) : PersonalInformationContract.Event()
        class LastNameChanged(val lastName: String) : PersonalInformationContract.Event()
        class DateChanged(val date: Long) : PersonalInformationContract.Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object GoBack : Effect()
        object CountrySelection : Effect()
        data class Dismiss(val resultCode: Int = Activity.RESULT_CANCELED) : Effect()
        data class ShowToast(val message: String) : Effect()
        data class DateSelection(val date: Calendar?) : Effect()
        data class StateSelection(val states: List<CountryState>) : Effect()
    }

    data class State(
        val profile: Profile? = null,
        val name: String = "",
        val lastName: String = "",
        val country: Country? = null,
        val state: CountryState? = null,
        val dateOfBirth: Calendar? = null,
        val confirmEnabled: Boolean = false,
        val loadingVisible: Boolean = false,
        val completedViewVisible: Boolean = false
    ) : RockWalletContract.State {

        val stateVisible: Boolean
            get() = state != null || !country?.states.isNullOrEmpty()
    }
}