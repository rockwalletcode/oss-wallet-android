package com.breadwallet.ui.kyccomingsoon

import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.M
import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.E
import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.F
import com.spotify.mobius.Next

interface KycComingSoonUpdateSpec {
    fun patch(model: M, event: E): Next<M, F> = when (event) {
        is E.OnBackToHomeClicked -> onBackToHomeClicked(model)
        is E.OnContactSupportClicked -> onContactSupportClicked(model)
    }

    fun onBackToHomeClicked(model: M): Next<M, F>
    fun onContactSupportClicked(model: M): Next<M, F>
}