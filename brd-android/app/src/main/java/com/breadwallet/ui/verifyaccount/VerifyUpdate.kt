package com.breadwallet.ui.verifyaccount

import com.spotify.mobius.Update
import com.breadwallet.ui.verifyaccount.VerifyScreen.M
import com.breadwallet.ui.verifyaccount.VerifyScreen.E
import com.breadwallet.ui.verifyaccount.VerifyScreen.F
import com.rockwallet.common.data.model.isKyc1
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*

object VerifyUpdate : Update<M, E, F>, VerifyScreenUpdateSpec {

    override fun update(model: M, event: E): Next<M, F> = patch(model, event)

    override fun onVerifyClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToAccountVerification))

    override fun onDismissClicked(model: M): Next<M, F> =
        dispatch(setOf(F.Dismiss))

    override fun onProfileDataLoaded(model: M, event: E.OnProfileDataLoaded): Next<M, F> =
        next(model.copy(hasKyc1 = event.profile?.isKyc1()))
}