/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 8/14/20.
 * Copyright (c) 2020 breadwallet LLC
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
package com.breadwallet.ui.provekey

import com.spotify.mobius.Next

interface PaperKeyProveUpdateSpec {
    fun patch(model: PaperKeyProve.M, event: PaperKeyProve.E): Next<PaperKeyProve.M, PaperKeyProve.F> = when (event) {
        PaperKeyProve.E.OnFaqClicked -> onFaqClicked(model)
        PaperKeyProve.E.OnBackClicked -> onBackClicked(model)
        PaperKeyProve.E.OnGotItClicked -> onGotItClicked(model)
        PaperKeyProve.E.OnWroteDownKeySaved -> onWroteDownKeySaved(model)
        is PaperKeyProve.E.OnFirstWordChanged -> onFirstWordChanged(model, event)
        is PaperKeyProve.E.OnSecondWordChanged -> onSecondWordChanged(model, event)
    }

    fun onFaqClicked(model: PaperKeyProve.M): Next<PaperKeyProve.M, PaperKeyProve.F>

    fun onBackClicked(model: PaperKeyProve.M): Next<PaperKeyProve.M, PaperKeyProve.F>

    fun onGotItClicked(model: PaperKeyProve.M): Next<PaperKeyProve.M, PaperKeyProve.F>

    fun onWroteDownKeySaved(model: PaperKeyProve.M): Next<PaperKeyProve.M, PaperKeyProve.F>

    fun onFirstWordChanged(model: PaperKeyProve.M, event: PaperKeyProve.E.OnFirstWordChanged): Next<PaperKeyProve.M, PaperKeyProve.F>

    fun onSecondWordChanged(model: PaperKeyProve.M, event: PaperKeyProve.E.OnSecondWordChanged): Next<PaperKeyProve.M, PaperKeyProve.F>
}