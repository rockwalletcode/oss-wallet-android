/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 8/12/19.
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
package com.breadwallet.ui.onboarding

import android.view.View
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.breadwallet.databinding.ControllerIntroBinding
import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.BaseController
import com.rockwallet.common.data.RockWalletApiConstants
import com.rockwallet.support.launchWebSite

private const val ANIMATION_MAX_DELAY = 15000L
private const val ANIMATION_DURATION = 3000L
private const val ICONS_TO_SHOW = 20

/**
 * Activity shown when there is no wallet, here the user can pick
 * between creating new wallet or recovering one with the paper key.
 */
class IntroController : BaseController() {

    private val binding by viewBinding(ControllerIntroBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        with(binding) {
            buttonNewWallet.setOnClickListener {
                EventUtils.pushEvent(EventUtils.EVENT_LANDING_PAGE_GET_STARTED)
                router.pushController(
                    RouterTransaction.with(OnBoardingController())
                        .popChangeHandler(HorizontalChangeHandler())
                        .pushChangeHandler(HorizontalChangeHandler())
                )
            }
            buttonRecoverWallet.setOnClickListener {
                EventUtils.pushEvent(EventUtils.EVENT_LANDING_PAGE_RESTORE_WALLET)
                router.pushController(
                    RouterTransaction.with(IntroRecoveryController())
                        .popChangeHandler(HorizontalChangeHandler())
                        .pushChangeHandler(HorizontalChangeHandler())
                )
            }
            faqButton.setOnClickListener {
                // CashSupport.Builder().detail(Topic.GET_STARTED).build().show(it)
                router.activity?.launchWebSite(RockWalletApiConstants.URL_SUPPORT_PAGE)
            }
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        EventUtils.pushEvent(EventUtils.EVENT_LANDING_PAGE_APPEARED)
//        startAnimations()
    }
}
