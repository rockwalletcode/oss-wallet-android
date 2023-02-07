package com.breadwallet.ui.kyccomingsoon

import com.spotify.mobius.Update
import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.M
import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.E
import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.F
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*

object KycComingSoonUpdate : Update<M, E, F>, KycComingSoonUpdateSpec {

    override fun update(model: M, event: E): Next<M, F> = patch(model, event)

    override fun onBackToHomeClicked(model: M): Next<M, F> =
        dispatch(setOf(F.Dismiss))

    override fun onContactSupportClicked(model: M): Next<M, F> =
        dispatch(setOf(F.ContactSupport))
}