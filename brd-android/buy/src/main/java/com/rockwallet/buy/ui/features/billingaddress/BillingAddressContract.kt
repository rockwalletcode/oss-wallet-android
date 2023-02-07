package com.rockwallet.buy.ui.features.billingaddress

import com.rockwallet.buy.ui.features.addcard.AddCardFlow
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.kyc.data.model.Country

class BillingAddressContract : RockWalletContract {

    sealed class Event : RockWalletContract.Event {
        object BackPressed : Event()
        object DismissClicked: Event()
        object CountryClicked: Event()
        object ConfirmClicked: Event()
        data class ZipChanged(val zip: String): Event()
        data class CityChanged(val city: String): Event()
        data class StateChanged(val state: String): Event()
        data class AddressChanged(val address: String): Event()
        data class LastNameChanged(val lastName: String): Event()
        data class FirstNameChanged(val firstName: String): Event()
        data class CountryChanged(val country: Country): Event()
        data class PaymentChallengeResult(val result: Int): Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object CountrySelection : Effect()
        data class PaymentMethod(val flow: AddCardFlow) : Effect()
        data class ShowError(val message: String): Effect()
        data class OpenWebsite(val url: String): Effect()
    }

    data class State(
        val firstName: String = "",
        val lastName: String = "",
        val zip: String = "",
        val city: String = "",
        val state: String? = null,
        val address: String = "",
        val country: Country? = null,
        val confirmEnabled: Boolean = false,
        val loadingIndicatorVisible: Boolean = false,
        val paymentReference: String? = null,
    ) : RockWalletContract.State
}