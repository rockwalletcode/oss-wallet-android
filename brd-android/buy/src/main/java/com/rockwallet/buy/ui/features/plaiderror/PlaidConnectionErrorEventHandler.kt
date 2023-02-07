package com.rockwallet.buy.ui.features.plaiderror

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface PlaidConnectionErrorEventHandler: RockWalletEventHandler<PlaidConnectionErrorContract.Event> {

    override fun handleEvent(event: PlaidConnectionErrorContract.Event) {
        return when (event) {
            is PlaidConnectionErrorContract.Event.TryAgainClicked -> onTryAgainClicked()
            is PlaidConnectionErrorContract.Event.ContactSupportClicked -> onContactSupportClicked()
        }
    }

    fun onTryAgainClicked()

    fun onContactSupportClicked()
}