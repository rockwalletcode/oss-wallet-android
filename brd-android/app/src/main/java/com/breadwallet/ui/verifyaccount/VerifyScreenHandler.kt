package com.breadwallet.ui.verifyaccount

import com.breadwallet.tools.security.ProfileManager
import com.breadwallet.ui.verifyaccount.VerifyScreen.F
import drewcarlson.mobius.flow.flowTransformer
import drewcarlson.mobius.flow.subtypeEffectHandler
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

fun createVerifyScreenHandler(
    profileManager: ProfileManager
) = subtypeEffectHandler<F, VerifyScreen.E> {
    addTransformer(handleLoadProfile(profileManager))
}

private fun handleLoadProfile(profileManager: ProfileManager) =
    flowTransformer<F.LoadProfileData, VerifyScreen.E> { effects ->
        effects.flatMapLatest { profileManager.profileChanges() }
            .mapLatest { profile ->
                VerifyScreen.E.OnProfileDataLoaded(profile)
            }
    }