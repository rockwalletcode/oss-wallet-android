package com.rockwallet.kyc.ui.features.takephoto

import android.net.Uri
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.kyc.data.enums.DocumentSide
import com.rockwallet.kyc.data.enums.DocumentType
import com.rockwallet.kyc.data.model.DocumentData
import com.rockwallet.kyc.ui.customview.PhotoFinderView

interface TakePhotoContract {

    sealed class Event : RockWalletContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object TakePhotoClicked : Event()
        object TakePhotoFailed : Event()
        class TakePhotoCompleted(val uri: Uri) : Event()
        class CameraPermissionResult(val granted: Boolean) : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object RequestCameraPermission : Effect()
        class ShowToast(val message: String) : Effect()
        class SetupCamera(val preferredLensFacing: Int) : Effect()

        class TakePhoto(
            val fileName: String
        ) : Effect()

        class GoToPreview(
            val currentData: DocumentData,
            val documentData: Array<DocumentData>,
            val documentType: DocumentType
        ) : Effect()
    }

    data class State(
        val description: Int,
        val imageUri: Uri? = null,
        val documentType: DocumentType,
        val documentSide: DocumentSide,
        val takePhotoEnabled: Boolean = true,
        val finderViewType: PhotoFinderView.Type,
        val preferredLensFacing: Int,
        val backEnabled: Boolean = true
    ) : RockWalletContract.State
}