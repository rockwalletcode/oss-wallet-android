package com.breadwallet.ui.provekeycompleted

import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.navigation.OnCompleteAction

object PaperKeyProveCompleted {

    data class M(
        val onComplete: OnCompleteAction
    ) {
        companion object {
            fun createDefault(onComplete: OnCompleteAction) = M(onComplete)
        }
    }

    sealed class E {
        object OnGoToWalletClicked : E()
    }

    sealed class F {
        object GoToHome : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Home
        }

        object GoToBuy : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Buy
        }

        data class TrackEvent(
            val eventName: String
        ) : F()
    }
}