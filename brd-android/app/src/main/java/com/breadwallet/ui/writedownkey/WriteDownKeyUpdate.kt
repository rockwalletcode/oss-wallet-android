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
package com.breadwallet.ui.writedownkey

import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.writedownkey.WriteDownKey.E
import com.breadwallet.ui.writedownkey.WriteDownKey.F
import com.breadwallet.ui.writedownkey.WriteDownKey.M
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update

object WriteDownKeyUpdate : Update<M, E, F>, WriteDownKeyUpdateSpec {

    override fun update(model: M, event: E) = patch(model, event)

    override fun onBackClicked(model: M): Next<M, F> =
        dispatch(setOf(F.ChangePage(model.page - 1)))

    override fun onNextClicked(model: M): Next<M, F> =
        dispatch(setOf(F.ChangePage(model.page + 1)))

    override fun onGotItClicked(model: M): Next<M, F> =
        dispatch(
            setOf(
                when {
                    model.requestAuth -> F.ShowAuthPrompt
                    else -> F.GetPhrase
                },
                F.TrackEvent(EventUtils.EVENT_PAPER_KEY_INTRO_GENEREATE_KEY)
            )
        )

    override fun onPageChanged(
        model: M,
        event: E.OnPageChanged
    ): Next<M, F> {
        return next(
            model.copy(
                page = event.page,
                isBackVisible = event.page > 1,
                isNextVisible = event.page < model.pageCount,
                isGotItVisible = event.page == model.pageCount
            )
        )
    }

    override fun onGetPhraseFailed(model: M): Next<M, F> =
        noChange()

    override fun onUserAuthenticated(model: M): Next<M, F> =
        dispatch(setOf(F.GetPhrase))

    override fun onPhraseRecovered(
        model: M,
        event: E.OnPhraseRecovered
    ): Next<M, F> =
        dispatch(setOf(F.GoToPaperKey(event.phrase, model.onComplete)))
}
