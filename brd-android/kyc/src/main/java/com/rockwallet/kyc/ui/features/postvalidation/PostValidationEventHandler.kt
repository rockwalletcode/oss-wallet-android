package com.rockwallet.kyc.ui.features.postvalidation

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface PostValidationEventHandler: RockWalletEventHandler<PostValidationContract.Event> {

    override fun handleEvent(event: PostValidationContract.Event) {
        return when (event) {
            is PostValidationContract.Event.ConfirmClicked -> onConfirmClicked()
        }
    }

    fun onConfirmClicked()
}