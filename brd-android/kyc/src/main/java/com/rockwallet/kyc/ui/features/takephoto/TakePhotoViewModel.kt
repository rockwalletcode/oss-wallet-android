package com.rockwallet.kyc.ui.features.takephoto

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.getString
import com.rockwallet.common.utils.toBundle
import com.rockwallet.kyc.R
import com.rockwallet.kyc.data.enums.DocumentSide
import com.rockwallet.kyc.data.enums.DocumentType
import com.rockwallet.kyc.data.model.DocumentData
import com.rockwallet.kyc.ui.customview.PhotoFinderView

class TakePhotoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<TakePhotoContract.State, TakePhotoContract.Event, TakePhotoContract.Effect>(
    application, savedStateHandle
), TakePhotoEventHandler, LifecycleObserver {

    private lateinit var arguments: TakePhotoFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = TakePhotoFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState(): TakePhotoContract.State {
        val documentSide = if (arguments.documentData.isEmpty()) {
            DocumentSide.FRONT
        } else {
            DocumentSide.BACK
        }
        val backDisabled =
            arguments.documentType == DocumentType.SELFIE || documentSide == DocumentSide.BACK

        return TakePhotoContract.State(
            description = getDescription(
                documentType = arguments.documentType,
                documentSide = documentSide
            ),
            documentSide = documentSide,
            documentType = arguments.documentType,
            preferredLensFacing = when (arguments.documentType) {
                DocumentType.SELFIE -> CameraSelector.LENS_FACING_FRONT
                else -> CameraSelector.LENS_FACING_BACK
            },
            finderViewType = when (arguments.documentType) {
                DocumentType.SELFIE -> PhotoFinderView.Type.SELFIE
                else -> PhotoFinderView.Type.DOCUMENT
            },
            backEnabled = !backDisabled
        )
    }

    override fun onBackClicked() {
        setEffect { TakePhotoContract.Effect.Back }
    }

    override fun onDismissCLicked() {
        setEffect { TakePhotoContract.Effect.Dismiss }
    }

    override fun onTakePhotoFailed() {
        setEffect {
            TakePhotoContract.Effect.ShowToast(
                getString(R.string.ErrorMessages_default)
            )
        }
    }

    override fun onCameraPermissionResult(granted: Boolean) {
        setEffect {
            if (granted) {
                TakePhotoContract.Effect.SetupCamera(
                    currentState.preferredLensFacing
                )
            } else {
                TakePhotoContract.Effect.RequestCameraPermission
            }
        }
    }

    override fun onTakePhotoCompleted(uri: Uri) {
        setEffect {
            TakePhotoContract.Effect.GoToPreview(
                currentData = DocumentData(
                    documentSide = currentState.documentSide,
                    imageUri = uri
                ),
                documentData = arguments.documentData,
                documentType = currentState.documentType
            )
        }
    }

    override fun onTakePhotoClicked() {
        setEffect {
            TakePhotoContract.Effect.TakePhoto(
                generateFileName(
                    type = currentState.documentType,
                    side = currentState.documentSide
                )
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected open fun onLifecycleStart() {
        val cameraPerm = ContextCompat.checkSelfPermission(
            getApplication<Application>().applicationContext, Manifest.permission.CAMERA
        )

        setEffect {
            if (cameraPerm == PackageManager.PERMISSION_GRANTED) {
                TakePhotoContract.Effect.SetupCamera(
                    currentState.preferredLensFacing
                )
            } else {
                TakePhotoContract.Effect.RequestCameraPermission
            }
        }
    }

    internal fun hasBackCamera(cameraProvider: ProcessCameraProvider?): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    internal fun hasFrontCamera(cameraProvider: ProcessCameraProvider?): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    private fun generateFileName(type: DocumentType, side: DocumentSide) =
        "image_${type.id}_${side.id}"

    private fun getDescription(documentType: DocumentType, documentSide: DocumentSide) =
        when (documentType) {
            DocumentType.SELFIE -> R.string.AccountKYCLevelTwo_TakeSelfieContent
            DocumentType.PASSPORT -> R.string.AccountKYCLevelTwo_CaptureEntireDocument
            else -> when (documentSide) {
                DocumentSide.BACK -> R.string.AccountKYCLevelTwo_CaptureBackPage
                DocumentSide.FRONT -> R.string.AccountKYCLevelTwo_CaptureFrontPage
            }
        }
}