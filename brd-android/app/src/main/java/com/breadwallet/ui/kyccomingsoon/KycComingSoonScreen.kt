package com.breadwallet.ui.kyccomingsoon

import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget

object KycComingSoonScreen {

    object M

    sealed class E {
        object OnBackToHomeClicked : E()
        object OnContactSupportClicked : E()
    }

    sealed class F {
        object Dismiss : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Back
        }

        object ContactSupport : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.ContactSupport
        }
    }
}