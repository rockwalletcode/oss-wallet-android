package com.rockwallet.kyc.ui.features.submitphoto

import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.kyc.data.enums.DocumentType
import com.rockwallet.kyc.data.model.DocumentData

interface SubmitPhotoContract {

    sealed class Event : RockWalletContract.Event {
        object BackClicked : Event()
        object RetakeClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object PostValidation : Effect()
        data class ShowToast(
            val message: String
        ) : Effect()
        data class TakePhoto(
            val documentData: Array<DocumentData>,
            val documentType: DocumentType
        ) : Effect()
    }

    data class State(
        val loadingVisible: Boolean = false,
        val currentData: DocumentData,
        val documentType: DocumentType,
        val documentData: Array<DocumentData>
    ) : RockWalletContract.State
}