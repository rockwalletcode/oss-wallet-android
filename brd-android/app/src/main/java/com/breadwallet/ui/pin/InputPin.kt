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
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.navigation.OnCompleteAction
import com.rockwallet.registration.ui.RegistrationFlow
import com.rockwallet.support.pages.Topic
import dev.zacsweers.redacted.annotations.Redacted

private const val PIN_LENGTH = 6

object InputPin {

    data class M(
        val mode: Mode = Mode.NEW,
        @Redacted val pin: String = "",
        @Redacted val pinConfirmation: String = "",
        val pinUpdateMode: Boolean = false,
        val skipWriteDownKey: Boolean = false,
        val onComplete: OnCompleteAction,
        val showBreadSignal: Boolean = false
    ) {

        companion object {
            fun createDefault(
                pinUpdateMode: Boolean,
                onComplete: OnCompleteAction,
                skipWriteDownKey: Boolean
            ) = M(
                pinUpdateMode = pinUpdateMode,
                onComplete = onComplete,
                skipWriteDownKey = skipWriteDownKey
            )
        }

        enum class Mode {
            VERIFY,  // Verify the old pin
            NEW,     // Chose a new pin
            CONFIRM  // Confirm the new pin
        }
    }

    sealed class E {

        object OnContinueToNextStep : E()
        object OnVerifyEmailClosed : E()
        data class OnVerifyEmailRequested(val email: String) : E()
        object OnFaqClicked : E()
        object OnBackClicked : E()
        object OnPinLocked : E()
        object OnPinSaved : E()
        object OnPinSaveFailed : E()
        object OnBreadSignalShown : E()

        data class OnPinEntered(
            @Redacted val pin: String,
            val isPinCorrect: Boolean
        ) : E()

        data class OnPinCheck(
            val hasPin: Boolean
        ) : E()
    }

    sealed class F {

        object AssociateNewDevice : F()
        object ContinueWithFlow : F()
        object CheckIfPinExists : F()
        object ResetPin : F(), ViewEffect
        data class SetupPin(
            @Redacted val pin: String
        ) : F() {
            init {
                require(pin.length == PIN_LENGTH) {
                    "pin must contain $PIN_LENGTH digits"
                }
            }
        }

        object ErrorShake : F(), ViewEffect
        object ShowPinError : F(), ViewEffect

        object GoToHome : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Home
        }
        data class GoToVerifyEmail(val email: String) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToRegistration(
                flow = RegistrationFlow.RE_VERIFY,
                email = email
            )
        }
        object GoToFaq : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.SupportDialog(Topic.PIN)
        }
        object GoBack : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Back
        }
        object GoToDisabledScreen : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.DisabledScreen
        }

        data class GoToWriteDownKey(val onComplete: OnCompleteAction) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.WriteDownKey(onComplete, false)
        }

        data class TrackEvent(val event: String) : F()

        data class ShowCompletedBottomSheet(val bottomSheetMsgId: Int) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Signal(
                titleResId = bottomSheetMsgId,
                messageResId = R.string.RecoveryKeyFlow_recoveryYourWallet,
                iconResId = R.drawable.ic_bottom_sheet_checkmark
            )
        }
    }
}
