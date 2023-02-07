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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import com.breadwallet.R
import com.breadwallet.databinding.ControllerWriteDownBinding
import com.breadwallet.databinding.ControllerWriteDownPageBinding
import com.breadwallet.tools.util.BRConstants
import com.breadwallet.ui.BaseController
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.auth.AuthenticationController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.navigation.OnCompleteAction
import com.breadwallet.ui.writedownkey.WriteDownKey.E
import com.breadwallet.ui.writedownkey.WriteDownKey.F
import com.breadwallet.ui.writedownkey.WriteDownKey.M
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.kodein.di.direct
import org.kodein.di.erased.instance

class WriteDownKeyController(args: Bundle? = null) :
    BaseMobiusController<M, E, F>(args),
    AuthenticationController.Listener {

    companion object {
        private const val EXTRA_ON_COMPLETE = "on-complete"
        private const val EXTRA_REQUEST_AUTH = "request-brd-auth"
    }

    constructor(
        doneAction: OnCompleteAction,
        requestAuth: Boolean
    ) : this(
        bundleOf(
            EXTRA_ON_COMPLETE to doneAction.name,
            EXTRA_REQUEST_AUTH to requestAuth
        )
    )

    init {
        registerForActivityResult(BRConstants.SHOW_PHRASE_REQUEST_CODE)
    }

    private val activeIndicator by lazy {
        ContextCompat.getDrawable(applicationContext!!, R.drawable.page_indicator_active)
    }
    private val inactiveIndicator by lazy {
        ContextCompat.getDrawable(applicationContext!!, R.drawable.page_indicator_inactive)
    }

    private val onComplete = OnCompleteAction.valueOf(arg(EXTRA_ON_COMPLETE))
    private val requestAuth = arg<Boolean>(EXTRA_REQUEST_AUTH)

    override val layoutId: Int = R.layout.controller_write_down
    override val defaultModel = M.createDefault(onComplete, requestAuth)
    override val init = WriteDownKeyInit
    override val update = WriteDownKeyUpdate
    override val flowEffectHandler
        get() = createWriteDownKeyHandler(direct.instance())

    private val binding by viewBinding(ControllerWriteDownBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        binding.viewPager.adapter = WriteDownPageAdapter()
    }

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with(binding) {
            merge(
                btnBack.clicks().map { E.OnBackClicked },
                btnNext.clicks().map { E.OnNextClicked },
                btnGotIt.clicks().map { E.OnGotItClicked },
                callbackFlow<E.OnPageChanged> {
                    val channel = channel
                    val listener = object : ViewPager.SimpleOnPageChangeListener() {
                        override fun onPageSelected(position: Int) {
                            channel.trySend(E.OnPageChanged(position + 1))
                        }
                    }
                    viewPager.addOnPageChangeListener(listener)
                    awaitClose {
                        viewPager.removeOnPageChangeListener(listener)
                    }
                }
            )
        }
    }

    override fun handleViewEffect(effect: ViewEffect) {
        when (effect) {
            is F.ChangePage ->
                binding.viewPager.currentItem = effect.newPage - 1
        }
    }

    override fun M.render() {
        with(binding) {
            ifChanged(M::page) { page ->
                listOf(indicator1, indicator2, indicator3)
                    .forEachIndexed { index, indicator ->
                        indicator.background = when (page) {
                            index + 1 -> activeIndicator
                            else -> inactiveIndicator
                        }
                    }
            }

            ifChanged(M::isNextVisible) {
                btnNext.isInvisible = !it
            }

            ifChanged(M::isBackVisible) {
                btnBack.isInvisible = !it
            }

            ifChanged(M::isGotItVisible) {
                btnGotIt.isInvisible = !it
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BRConstants.SHOW_PHRASE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            eventConsumer.accept(E.OnUserAuthenticated)
        }
    }

    override fun onAuthenticationSuccess() {
        eventConsumer.accept(E.OnUserAuthenticated)
    }

    inner class WriteDownPageAdapter : RouterPagerAdapter(this) {
        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                val root = when (position) {
                    0 -> PageOneController()
                    1 -> PageTwoController()
                    2 -> PageThreeController()
                    else -> error("Unknown position")
                }
                router.setRoot(RouterTransaction.with(root))
            }
        }

        override fun getCount(): Int = 3
    }
}

class PageOneController(args: Bundle? = null) : BaseController(args) {
    private val binding by viewBinding(ControllerWriteDownPageBinding::inflate)
    override fun onCreateView(view: View) {
        super.onCreateView(view)

        with(binding) {
            ivIcon.setImageResource(R.drawable.ic_recovery_onboarding_1)
            tvTitle.setText(R.string.RecoveryKeyOnboarding_title1)
            tvDescription.setText(R.string.RecoveryKeyOnboarding_description1)
        }
    }
}

class PageTwoController(args: Bundle? = null) : BaseController(args) {
    private val binding by viewBinding(ControllerWriteDownPageBinding::inflate)
    override fun onCreateView(view: View) {
        super.onCreateView(view)

        with(binding) {
            ivIcon.setImageResource(R.drawable.ic_recovery_onboarding_2)
            tvTitle.setText(R.string.RecoveryKeyOnboarding_title2)
            tvDescription.setText(R.string.RecoveryKeyOnboarding_description2)
        }
    }
}

class PageThreeController(args: Bundle? = null) : BaseController(args) {
    private val binding by viewBinding(ControllerWriteDownPageBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)

        with(binding) {
            ivIcon.setImageResource(R.drawable.ic_recovery_onboarding_3)
            tvTitle.setText(R.string.RecoveryKeyOnboarding_title3)
            tvDescription.setText(R.string.RecoveryKeyOnboarding_description3)
        }
    }
}
