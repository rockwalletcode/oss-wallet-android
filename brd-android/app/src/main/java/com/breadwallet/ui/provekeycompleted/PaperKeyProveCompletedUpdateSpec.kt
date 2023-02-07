package com.breadwallet.ui.provekeycompleted

import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.M
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.E
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.F
import com.spotify.mobius.Next

interface PaperKeyProveCompletedUpdateSpec {
    fun patch(model: M, event: E): Next<M, F> = when (event) {
        is E.OnGoToWalletClicked -> onGoToWalletClicked(model)
    }

    fun onGoToWalletClicked(model: M): Next<M, F>
}