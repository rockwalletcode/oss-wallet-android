package com.breadwallet.ui.resetpin

import com.breadwallet.R
import com.breadwallet.ui.resetpin.ResetPinInput.E
import com.breadwallet.ui.resetpin.ResetPinInput.F
import com.breadwallet.ui.resetpin.ResetPinInput.M
import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

object ResetPinInputUpdate : Update<M, E, F>, ResetPinInputUpdateSpec {

    override fun update(
        model: M,
        event: E
    ) = patch(model, event)

    override fun onBackClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoBack))

    override fun onFaqClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToFaq))

    override fun onPinEntered(
        model: M,
        event: E.OnPinEntered
    ): Next<M, F> {
        return when (model.mode) {
            M.Mode.CONFIRM -> if (event.pin == model.pin) {
                next(model, setOf<F>(F.SetupPin(model.pin)))
            } else {
                next(
                    model.copy(mode = M.Mode.NEW, pin = ""),
                    setOf<F>(F.ErrorShake, F.ResetPin)
                )
            }
            M.Mode.NEW -> {
                next(
                    model.copy(mode = M.Mode.CONFIRM, pin = event.pin),
                    setOf<F>(F.ResetPin)
                )
            }
        }
    }

    override fun onBreadSignalShown(model: M): Next<M, F> {
        return next(model, setOf(F.GoToResetCompleted))
    }

    override fun onPinLocked(model: M): Next<M, F> =
        next(model, setOf(F.GoToDisabledScreen))

    override fun onPinSaved(model: M): Next<M, F> {
        return next(
            model.copy(showBreadSignal = true),
            setOf(F.ShowCompletedBottomSheet(R.string.UpdatePin_bottomSheetMsg))
        )
    }

    override fun onPinSaveFailed(model: M): Next<M, F> {
        return next(model.copy(mode = M.Mode.NEW, pin = ""), setOf<F>(F.ShowPinError))
    }
}
