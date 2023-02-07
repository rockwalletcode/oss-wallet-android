package com.rockwallet.buy.ui.features.orderpreview

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface OrderPreviewEventHandler: RockWalletEventHandler<OrderPreviewContract.Event> {

    override fun handleEvent(event: OrderPreviewContract.Event) {
        return when (event) {
            is OrderPreviewContract.Event.OnBackPressed -> onBackClicked()
            is OrderPreviewContract.Event.OnConfirmClicked -> onConfirmClicked()
            is OrderPreviewContract.Event.OnDismissClicked -> onDismissClicked()
            is OrderPreviewContract.Event.OnCreditInfoClicked -> onCreditInfoClicked()
            is OrderPreviewContract.Event.OnNetworkInfoClicked -> onNetworkInfoClicked()
            is OrderPreviewContract.Event.OnTermsChanged -> onTermsChanged(event.accepted)
            is OrderPreviewContract.Event.OnSecurityCodeChanged -> onSecurityCodeChanged(event.securityCode)
            is OrderPreviewContract.Event.OnSecurityCodeInfoClicked -> onSecurityCodeInfoClicked()
            is OrderPreviewContract.Event.OnTermsAndConditionsClicked -> onTermsAndConditionsClicked()
            is OrderPreviewContract.Event.OnUserAuthenticationSucceed -> onUserAuthenticationSucceed()
        }
    }

    fun onBackClicked()

    fun onDismissClicked()

    fun onConfirmClicked()

    fun onCreditInfoClicked()

    fun onNetworkInfoClicked()

    fun onSecurityCodeInfoClicked()

    fun onTermsAndConditionsClicked()

    fun onUserAuthenticationSucceed()

    fun onTermsChanged(accepted: Boolean)

    fun onSecurityCodeChanged(securityCode: String)
}