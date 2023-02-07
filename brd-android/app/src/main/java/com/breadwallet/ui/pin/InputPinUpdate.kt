/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 9/23/19.
 * Copyright (c) 2019 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.ui.pin

import com.breadwallet.R
import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.pin.InputPin.E
import com.breadwallet.ui.pin.InputPin.F
import com.breadwallet.ui.pin.InputPin.M
import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

object InputPinUpdate : Update<M, E, F>, InputPinUpdateSpec {

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
            M.Mode.VERIFY -> if (event.isPinCorrect) {
                next(model.copy(pinUpdateMode = true, mode = M.Mode.NEW))
            } else {
                next(model, setOf<F>(F.ErrorShake, F.ResetPin))
            }
            M.Mode.NEW -> {
                next(model.copy(mode = M.Mode.CONFIRM, pin = event.pin), setOf<F>(F.ResetPin))
            }
            M.Mode.CONFIRM -> if (event.pin == model.pin) {
                next(model, setOf<F>(F.SetupPin(model.pin)))
            } else {
                next(
                    model.copy(mode = M.Mode.NEW, pin = ""),
                    setOf<F>(F.ErrorShake, F.ResetPin)
                )
            }
        }
    }

    override fun onPinLocked(model: M): Next<M, F> =
        next(model, setOf(F.GoToDisabledScreen))

    override fun onPinSaved(model: M): Next<M, F> {
        val effect = if (model.mode == M.Mode.CONFIRM && !model.pinUpdateMode) {
            F.AssociateNewDevice
        } else {
            F.ContinueWithFlow
        }
        return next(model, setOf(effect))
    }

    override fun onContinueToNextStep(model: M): Next<M, F> {
        return if (model.pinUpdateMode || model.skipWriteDownKey) {
            next(
                model.copy(showBreadSignal = true),
                setOf(F.ShowCompletedBottomSheet(R.string.UpdatePin_bottomSheetMsg))
            )
        } else {
            next(
                model.copy(showBreadSignal = true),
                setOf(F.ShowCompletedBottomSheet(R.string.SetPin_bottomSheetMsg)),
            )
        }
    }

    override fun onVerifyEmailClosed(model: M): Next<M, F> =
        dispatch(setOf(F.GoToHome))

    override fun onVerifyEmailRequested(model: M, event: E.OnVerifyEmailRequested): Next<M, F> =
        dispatch(setOf(F.GoToVerifyEmail(event.email)))

    override fun onPinSaveFailed(model: M): Next<M, F> {
        return next(model.copy(mode = M.Mode.NEW, pin = ""), setOf<F>(F.ShowPinError))
    }

    override fun onPinCheck(
        model: M,
        event: E.OnPinCheck
    ): Next<M, F> {
        val mode = if (event.hasPin) M.Mode.VERIFY else M.Mode.NEW
        return next(model.copy(mode = mode))
    }

    override fun onBreadSignalShown(model: M): Next<M, F> {
        val effects = if (model.pinUpdateMode || model.skipWriteDownKey) {
            setOf(F.GoToHome)
        } else {
            setOf(
                F.GoToWriteDownKey(model.onComplete),
                F.TrackEvent(EventUtils.EVENT_ONBOARDING_PIN_CREATED)
            )
        }
        return next(model, effects)
    }
}
