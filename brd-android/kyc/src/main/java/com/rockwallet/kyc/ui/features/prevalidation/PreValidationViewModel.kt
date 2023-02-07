package com.rockwallet.kyc.ui.features.prevalidation

import android.app.Application
import com.rockwallet.common.ui.base.RockWalletViewModel

class PreValidationViewModel(
    application: Application

) : RockWalletViewModel<PreValidationContract.State, PreValidationContract.Event, PreValidationContract.Effect>(
    application
), PreValidationEventHandler {
    override fun createInitialState() = PreValidationContract.State

    override fun onBackClicked() {
        setEffect { PreValidationContract.Effect.Back }
    }

    override fun onConfirmClicked() {
        setEffect { PreValidationContract.Effect.ProofOfIdentity }
    }

    override fun onDismissCLicked() {
        setEffect { PreValidationContract.Effect.Dismiss }
    }
}