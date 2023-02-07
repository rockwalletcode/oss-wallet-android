package com.breadwallet.ui.verifyaccount

import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.rockwallet.common.data.model.Profile

object VerifyScreen {

    data class M(
        val hasKyc1: Boolean? = null
    )

    sealed class E {
        object OnVerifyClicked : E()
        object OnDismissClicked : E()
        data class OnProfileDataLoaded(val profile: Profile?) : E()
    }

    sealed class F {
        object LoadProfileData : F()
        object GoToAccountVerification : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToKyc
        }

        object Dismiss : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Home
        }
    }
}