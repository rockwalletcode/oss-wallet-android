package com.rockwallet.kyc.ui.features.prevalidation

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface PreValidationEventHandler: RockWalletEventHandler<PreValidationContract.Event> {

    override fun handleEvent(event: PreValidationContract.Event) {
        return when (event) {
            is PreValidationContract.Event.BackClicked -> onBackClicked()
            is PreValidationContract.Event.ConfirmClicked -> onConfirmClicked()
            is PreValidationContract.Event.DismissCLicked -> onDismissCLicked()
        }
    }

    fun onBackClicked()

    fun onConfirmClicked()

    fun onDismissCLicked()
}