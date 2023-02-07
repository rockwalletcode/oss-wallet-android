package com.rockwallet.trade.ui.features.authentication

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface SwapAuthenticationEventHandler: RockWalletEventHandler<SwapAuthenticationContract.Event> {

    override fun handleEvent(event: SwapAuthenticationContract.Event) {
        return when (event) {
            is SwapAuthenticationContract.Event.DismissClicked -> onDismissClicked()
            is SwapAuthenticationContract.Event.AuthSucceeded -> onAuthSucceeded()
            is SwapAuthenticationContract.Event.AuthFailed -> onAuthFailed(event.errorCode)
            is SwapAuthenticationContract.Event.PinValidated -> onPinValidated(event.valid)
        }
    }

    fun onDismissClicked()

    fun onAuthSucceeded()

    fun onAuthFailed(errorCode: Int)

    fun onPinValidated(valid: Boolean)
}