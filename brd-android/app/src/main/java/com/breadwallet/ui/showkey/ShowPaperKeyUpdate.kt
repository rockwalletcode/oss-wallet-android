/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 10/10/19.
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
package com.breadwallet.ui.showkey

import com.breadwallet.R
import com.breadwallet.ui.navigation.OnCompleteAction
import com.breadwallet.ui.showkey.ShowPaperKey.F
import com.breadwallet.ui.showkey.ShowPaperKey.E
import com.breadwallet.ui.showkey.ShowPaperKey.M
import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

object ShowPaperKeyUpdate : Update<M, E, F>,
    ShowPaperKeyUpdateSpec {

    override fun update(
        model: M,
        event: E
    ): Next<M, F> = patch(model, event)

    override fun onPageChanged(
        model: M,
        event: E.OnPageChanged
    ): Next<M, F> {
        return next(
            model.copy(
                currentWord = event.position,
                warningMessage = when {
                    event.position < model.phrase.size - 1 -> R.string.WritePaperPhrase_warning
                    else -> R.string.WritePaperPhrase_warning2
                },
                doneButtonEnabled = model.doneButtonEnabled || event.position == model.phrase.size - 1
            )
        )
    }

    override fun onBackClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoBack))

    override fun onDoneClicked(model: M): Next<M, F> {
        val effect: F = if (model.onComplete == null) {
            if (model.phraseWroteDown) {
                F.GoBack
            } else {
                F.GoToPaperKeyProve(model.phrase, OnCompleteAction.GO_HOME)
            }
        } else {
            F.GoToPaperKeyProve(model.phrase, model.onComplete)
        }
        return dispatch(setOf(effect))
    }

    override fun onSkipClicked(
        model: M
    ): Next<M, F> {
        val effect = when (model.onComplete) {
            OnCompleteAction.GO_HOME -> F.GoToHome
            OnCompleteAction.GO_TO_BUY -> F.GoToBuy
            null -> F.GoBack
        } as F
        return dispatch(setOf(effect))
    }
}
