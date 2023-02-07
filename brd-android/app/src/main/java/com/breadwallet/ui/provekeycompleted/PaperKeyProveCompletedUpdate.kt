package com.breadwallet.ui.provekeycompleted

import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.navigation.OnCompleteAction
import com.spotify.mobius.Update
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.M
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.E
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.F
import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch

object PaperKeyProveCompletedUpdate : Update<M, E, F>, PaperKeyProveCompletedUpdateSpec {

    override fun update(model: M, event: E): Next<M, F> = patch(model, event)

    override fun onGoToWalletClicked(model: M): Next<M, F> =
        dispatch(
            setOf(
                when (model.onComplete) {
                    OnCompleteAction.GO_TO_BUY -> F.GoToBuy
                    OnCompleteAction.GO_HOME -> F.GoToHome
                } as F,
                F.TrackEvent(EventUtils.EVENT_ONBOARDING_COMPLETE)
            )
        )
}