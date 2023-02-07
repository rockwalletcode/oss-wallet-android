package com.rockwallet.kyc.ui.features.proofofidentity

import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.kyc.data.enums.DocumentType

interface ProofOfIdentityContract {

    sealed class Event : RockWalletContract.Event {
        object LoadDocuments : Event()
        object BackClicked : Event()
        object IdCardClicked : Event()
        object PassportClicked : Event()
        object DrivingLicenceClicked : Event()
        object ResidencePermitClicked : Event()
        object DismissClicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        data class ShowToast(val message: String) : Effect()
        data class GoToDocumentUpload(val documentType: DocumentType) : Effect()
    }

    data class State(
        val idCardVisible: Boolean = false,
        val passportVisible: Boolean = false,
        val drivingLicenceVisible: Boolean = false,
        val residencePermitVisible: Boolean = false,
        val initialLoadingVisible: Boolean = false
    ) : RockWalletContract.State
}