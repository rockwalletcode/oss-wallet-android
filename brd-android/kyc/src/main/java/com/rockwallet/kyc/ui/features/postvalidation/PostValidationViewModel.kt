package com.rockwallet.kyc.ui.features.postvalidation

import android.app.Application
import com.breadwallet.tools.security.ProfileManager
import com.rockwallet.common.ui.base.RockWalletViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class PostValidationViewModel(
    application: Application
) : RockWalletViewModel<PostValidationContract.State, PostValidationContract.Event, PostValidationContract.Effect>(
    application
), PostValidationEventHandler, KodeinAware {

    override val kodein by closestKodein { application }
    private val profileManager by kodein.instance<ProfileManager>()

    init {
        profileManager.updateProfile()
    }

    override fun createInitialState() = PostValidationContract.State

    override fun onConfirmClicked() {
        setEffect { PostValidationContract.Effect.Profile }
    }
}