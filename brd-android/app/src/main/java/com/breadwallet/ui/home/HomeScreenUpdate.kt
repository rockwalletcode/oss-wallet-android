/**
 * BreadWallet
 *
 * Created by Ahsan Butt <ahsan.butt@breadwallet.com> on 8/1/19.
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
package com.breadwallet.ui.home

import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.home.HomeScreen.E
import com.breadwallet.ui.home.HomeScreen.F
import com.breadwallet.ui.home.HomeScreen.M
import com.rockwallet.common.data.enums.AccessRestrictionReason
import com.rockwallet.common.data.model.*
import com.platform.tools.SessionHolder
import com.spotify.mobius.Effects.effects
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Next.noChange
import com.spotify.mobius.Update

const val MAX_CRYPTO_DIGITS = 8
const val DIALOG_PARTNERSHIP_NOTE_BUY = "buy_part_note"

val HomeScreenUpdate = Update<M, E, F> { model, event ->
    when (event) {
        is E.OnWalletDisplayOrderUpdated -> next(
            model.copy(
                wallets = model.wallets.values
                    .sortedBy { event.displayOrder.indexOf(it.currencyId) }
                    .associateBy(Wallet::currencyCode)
            ),
            setOf(F.UpdateWalletOrder(event.displayOrder))
        )
        is E.OnBuyBellNeededLoaded -> next(model.copy(isBuyBellNeeded = event.isBuyBellNeeded))
        is E.OnEnabledWalletsUpdated -> {
            next(
                model.copy(
                    wallets = event.wallets
                        .associateBy(Wallet::currencyCode)
                        .mapValues { (currencyCode, wallet) ->
                            model.wallets[currencyCode] ?: wallet
                        },
                    displayOrder = event.wallets.map(Wallet::currencyId)
                ),
                if (model.wallets.isEmpty()) {
                    setOf<F>(F.LoadWallets)
                } else {
                    emptySet()
                }
            )
        }
        is E.OnWalletsUpdated -> {
            val wallets = model.wallets.toMutableMap()
            event.wallets.forEach { wallet ->
                wallets[wallet.currencyCode] = wallet.copy(
                    isSyncing = wallet.isSyncing,
                )
            }
            next(model.copy(wallets = wallets))
        }
        is E.OnConnectionUpdated -> next(model.copy(hasInternet = event.isConnected))
        is E.OnWalletClicked -> {
            val wallet = model.wallets[event.currencyCode]
            when (wallet?.state) {
                null, Wallet.State.LOADING -> noChange<M, F>()
                else -> dispatch<M, F>(
                    effects(
                        F.GoToWallet(event.currencyCode)
                    )
                )
            }
        }
        is E.OnAddWalletsClicked -> dispatch(effects(F.GoToAddWallet))
        E.OnBuyClicked -> dispatch(
            effects(
                when {
                    model.profile.isRegistrationNeeded() -> F.GoToRegistration
                    model.profile.isEmailVerificationNeeded() || !SessionHolder.isUserSessionVerified() ->
                        F.RequestSessionVerification
                    !model.hasInternet -> F.GoToNoInternetScreen
                    !model.profile.isBuyAllowed() -> when (model.profile.getRestrictionReason()) {
                        null, AccessRestrictionReason.KYC -> F.GoToVerifyProfileBuy
                        AccessRestrictionReason.STATE, AccessRestrictionReason.COUNTRY -> F.GoToComingSoon
                    }
                    else -> F.GoToBuy
                }
            )
        )
        E.OnTradeClicked -> dispatch(
            effects(
                when {
                    model.profile.isRegistrationNeeded() -> F.GoToRegistration
                    model.profile.isEmailVerificationNeeded() || !SessionHolder.isUserSessionVerified() ->
                        F.RequestSessionVerification
                    !model.hasInternet -> F.GoToNoInternetScreen
                    !model.profile.isSwapAllowed() -> when (model.profile.getRestrictionReason()) {
                        null, AccessRestrictionReason.KYC -> F.GoToVerifyProfileBuy
                        AccessRestrictionReason.STATE, AccessRestrictionReason.COUNTRY -> F.GoToComingSoon
                    }
                    else -> F.GoToTrade
                }
            )
	    )
        E.OnBuyNoteSeen -> dispatch(effects(F.GoToBuy))
        E.OnMenuClicked -> dispatch(effects(F.GoToMenu))
        E.OnProfileClicked -> dispatch(
            effects(
                when {
                    model.profile.isRegistrationNeeded() -> F.GoToRegistration
                    model.profile.isEmailVerificationNeeded() || !SessionHolder.isUserSessionVerified() ->
                        F.RequestSessionVerification
                    else -> F.GoToProfile
                }
            )
        )

        E.OnEmailVerified -> {
            dispatch(
                setOf(
                    F.RefreshProfile,
                    F.GoToProfile
                )
            )
        }

        is E.OnProfileDataLoaded -> next(
            model.copy(profile = event.profile)
        )

        is E.OnProfileDataLoadFailed -> noChange()

        is E.OnPromptLoaded -> next(model.copy(promptId = event.promptId))
        is E.OnDeepLinkProvided -> dispatch(
            effects(
                F.GoToDeepLink(
                    event.url
                )
            )
        )
        is E.OnInAppNotificationProvided -> dispatch(
            effects(
                F.GoToInappMessage(
                    event.inAppMessage
                )
            )
        )
        is E.OnPushNotificationOpened -> dispatch(
            effects(
                F.RecordPushNotificationOpened(
                    event.campaignId
                )
            )
        )
        is E.OnShowBuyAndSell -> {
            val clickAttributes =
                mapOf(EventUtils.EVENT_ATTRIBUTE_BUY_AND_SELL to model.showBuyAndSell.toString())
            next<M, F>(
                model.copy(showBuyAndSell = event.showBuyAndSell),
                effects(
                    F.TrackEvent(
                        EventUtils.EVENT_HOME_DID_TAP_BUY,
                        clickAttributes
                    )
                )
            )
        }
        is E.OnPromptDismissed -> {
            val promptName = when (event.promptId) {
                PromptItem.FINGER_PRINT -> EventUtils.PROMPT_TOUCH_ID
                PromptItem.PAPER_KEY -> EventUtils.PROMPT_PAPER_KEY
                PromptItem.UPGRADE_PIN -> EventUtils.PROMPT_UPGRADE_PIN
                PromptItem.RECOMMEND_RESCAN -> EventUtils.PROMPT_RECOMMEND_RESCAN
                PromptItem.RATE_APP -> EventUtils.PROMPT_RATE_APP
                PromptItem.VERIFY_USER -> EventUtils.PROMPT_VERIFY_USER
            }
            val eventName = promptName + EventUtils.EVENT_PROMPT_SUFFIX_DISMISSED
            val effects = mutableSetOf(F.DismissPrompt(event.promptId), F.TrackEvent(eventName))
            if (event.promptId == PromptItem.RATE_APP) effects.add(F.ClearRateAppPrompt)
            next(
                model.copy(promptId = null),
                effects
            )
        }
        is E.CheckForPrompt -> dispatch(setOf<F>(F.LoadPrompt))
        E.OnFingerprintPromptClicked -> {
            val eventName = EventUtils.PROMPT_TOUCH_ID + EventUtils.EVENT_PROMPT_SUFFIX_TRIGGER
            next(
                model.copy(promptId = null),
                effects(
                    F.DismissPrompt(PromptItem.FINGER_PRINT),
                    F.GoToFingerprintSettings,
                    F.TrackEvent(eventName)
                )
            )
        }
        E.OnPaperKeyPromptClicked -> {
            val eventName = EventUtils.PROMPT_PAPER_KEY + EventUtils.EVENT_PROMPT_SUFFIX_TRIGGER
            next(
                model.copy(promptId = null),
                effects(
                    F.GoToWriteDownKey,
                    F.TrackEvent(eventName)
                )
            )
        }
        E.OnUpgradePinPromptClicked -> {
            val eventName = EventUtils.PROMPT_UPGRADE_PIN + EventUtils.EVENT_PROMPT_SUFFIX_TRIGGER
            next(
                model.copy(promptId = null),
                effects(
                    F.GoToUpgradePin,
                    F.TrackEvent(eventName)
                )
            )
        }
        E.OnRescanPromptClicked -> {
            val eventName =
                EventUtils.PROMPT_RECOMMEND_RESCAN + EventUtils.EVENT_PROMPT_SUFFIX_TRIGGER
            next(
                model.copy(promptId = null),
                effects(
                    F.StartRescan,
                    F.TrackEvent(eventName)
                )
            )
        }
        E.OnRateAppPromptClicked -> {
            next(
                model.copy(promptId = null),
                effects(
                    F.GoToGooglePlay
                )
            )
        }
        is E.OnRateAppPromptDontShowClicked -> {
            next(model.copy(rateAppPromptDontShowMeAgain = event.checked))
        }
        E.OnRateAppPromptNoThanksClicked -> {
            val effects = mutableSetOf<F>(F.GoToSupportDialog)
            if (model.rateAppPromptDontShowMeAgain) {
                effects.add(F.SaveDontShowMeRateAppPrompt)
            }
            dispatch(effects)
        }

        is E.OnPositiveDialogClicked -> {
            dispatch(effects(F.GoToFeedback))
        }

        is E.OnManageAssetsRequested -> {
            dispatch(effects(F.GoToAddWallet))
        }

        is E.OnVerifyPromptClicked -> {
            when {
                model.profile.isRegistrationNeeded() -> dispatch(effects(F.GoToRegistration))
                else -> dispatch(effects(F.GoToKyc))
            }
        }
    }
}
