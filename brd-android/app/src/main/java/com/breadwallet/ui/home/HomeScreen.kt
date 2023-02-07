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

import com.breadwallet.R
import com.breadwallet.model.InAppMessage
import com.breadwallet.model.PriceChange
import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.navigation.OnCompleteAction
import com.breadwallet.ui.settings.SettingsSection
import com.rockwallet.common.data.model.Profile
import com.rockwallet.common.ui.dialog.RockWalletGenericDialogArgs
import com.rockwallet.registration.ui.RegistrationFlow
import dev.zacsweers.redacted.annotations.Redacted
import java.math.BigDecimal

object HomeScreen {

    const val SUPPORT_FORM_DIALOG = "rock_wallet_support_dialog"
    const val SUPPORT_FORM_DIALOG_POSITIVE = "rock_wallet_support_dialog_pos"
    const val SUPPORT_FORM_DIALOG_NEGATIVE = "rock_wallet_support_dialog_neg"

    data class M(
        val wallets: Map<String, Wallet> = emptyMap(),
        val displayOrder: List<String> = emptyList(),
        val promptId: PromptItem? = null,
        val hasInternet: Boolean = true,
        val isBuyBellNeeded: Boolean = false,
        val showBuyAndSell: Boolean = false,
        val rateAppPromptDontShowMeAgain: Boolean = false,
        val profile: Profile? = null
    ) {

        companion object {
            fun createDefault() = M()
        }

        val aggregatedFiatBalance: BigDecimal = wallets.values
            .fold(BigDecimal.ZERO) { acc, next ->
                acc.add(next.fiatBalance)
            }

        val showPrompt: Boolean = promptId != null
    }

    sealed class E {
        data class OnEnabledWalletsUpdated(@Redacted val wallets: List<Wallet>) : E()

        data class OnWalletsUpdated(@Redacted val wallets: List<Wallet>) : E()

        data class OnWalletDisplayOrderUpdated(@Redacted val displayOrder: List<String>) : E()

        data class OnBuyBellNeededLoaded(val isBuyBellNeeded: Boolean) : E()

        data class OnConnectionUpdated(val isConnected: Boolean) : E()

        data class OnWalletClicked(val currencyCode: String) : E()

        object OnAddWalletsClicked : E()

        object OnBuyClicked : E()
        object OnBuyNoteSeen : E()
        object OnTradeClicked : E()
        object OnMenuClicked : E()
        object OnProfileClicked : E()

        data class OnDeepLinkProvided(val url: String) : E()
        data class OnInAppNotificationProvided(val inAppMessage: InAppMessage) : E()

        data class OnPromptLoaded(val promptId: PromptItem?) : E()

        data class OnPushNotificationOpened(val campaignId: String) : E()

        data class OnShowBuyAndSell(val showBuyAndSell: Boolean) : E()

        object CheckForPrompt : E()
        data class OnPromptDismissed(val promptId: PromptItem) : E()
        object OnFingerprintPromptClicked : E()
        object OnPaperKeyPromptClicked : E()
        object OnUpgradePinPromptClicked : E()
        object OnRescanPromptClicked : E()
        object OnRateAppPromptClicked : E()
        object OnEmailVerified : E()
        object OnManageAssetsRequested : E()
        data class OnRateAppPromptDontShowClicked(val checked: Boolean) : E()
        object OnRateAppPromptNoThanksClicked : E()
        object OnPositiveDialogClicked : E()
        data class OnProfileDataLoaded(val profile: Profile) : E()
        data class OnProfileDataLoadFailed(val message: String?) : E()
        object OnVerifyPromptClicked : E()
    }

    sealed class F {

        object LoadProfile : F()
        object RefreshProfile : F()
        object LoadWallets : F()
        object LoadEnabledWallets : F()
        object LoadIsBuyBellNeeded : F()
        object LoadPrompt : F()
        object LoadConnectivityState : F()
        object CheckInAppNotification : F()
        object CheckIfShowBuyAndSell : F()
        object RequestSessionVerification : F()
        object GoToNoInternetScreen : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.NoInternetScreen
        }

        data class GoToDeepLink(val url: String) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.DeepLink(url, true)
        }

        data class GoToInappMessage(val inAppMessage: InAppMessage) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToInAppMessage(inAppMessage)
        }

        data class GoToWallet(val currencyCode: String) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Wallet(currencyCode)
        }

        object GoToAddWallet : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.AddWallet
        }

        object GoToBuy : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Buy
        }

        object GoToTrade : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Trade
        }

        object GoToMenu : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Menu(SettingsSection.HOME)
        }

        object GoToProfile : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Profile
        }

        object GoToComingSoon : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.KycComingSoon
        }

       object GoToVerifyProfileBuy : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.VerifyProfile(
                NavigationTarget.VerifyProfile.Type.BUY
            )
        }

       object GoToVerifyProfileSwap : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.VerifyProfile(
                NavigationTarget.VerifyProfile.Type.SWAP
            )
       }

        object GoToRegistration : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToRegistration(RegistrationFlow.REGISTER)
        }

        object GoToFingerprintSettings : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.FingerprintSettings
        }

        object GoToWriteDownKey : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.WriteDownKey(
                OnCompleteAction.GO_HOME
            )
        }

        object GoToUpgradePin : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.SetPin()
        }

        object GoToGooglePlay : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.ReviewBrd
        }

        object GoToSupportDialog : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.RockWalletGenericDialog(
                RockWalletGenericDialogArgs(
                    titleRes = R.string.SupportForm_helpUsImprove,
                    descriptionRes = R.string.SupportForm_feedbackAppreciated,
                    positive = RockWalletGenericDialogArgs.ButtonData(
                        titleRes = R.string.Button_submit,
                        resultKey = SUPPORT_FORM_DIALOG_POSITIVE
                    ),
                    negative = RockWalletGenericDialogArgs.ButtonData(
                        titleRes = R.string.SupportForm_notNow,
                        resultKey = SUPPORT_FORM_DIALOG_NEGATIVE
                    ),
                    requestKey = SUPPORT_FORM_DIALOG
                )
            )
        }

        data class RecordPushNotificationOpened(val campaignId: String) : F()

        data class UpdateWalletOrder(
            val orderedCurrencyIds: List<String>
        ) : F()

        data class TrackEvent(
            val eventName: String,
            val attributes: Map<String, String>? = null
        ) : F()

        data class DismissPrompt(val promptItem: PromptItem) : F()

        object StartRescan : F()

        object ClearRateAppPrompt : F()
        object SaveDontShowMeRateAppPrompt : F()

        object GoToKyc : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToKyc
        }

        object GoToFeedback : F(), NavigationEffect{
            override val navigationTarget = NavigationTarget.GoToFeedback
        }
    }
}

data class Wallet(
    val currencyId: String,
    val currencyName: String,
    val currencyCode: String,
    val fiatPricePerUnit: BigDecimal = BigDecimal.ZERO,
    val balance: BigDecimal = BigDecimal.ZERO,
    val fiatBalance: BigDecimal = BigDecimal.ZERO,
    val isSyncing: Boolean = false,
    val priceChange: PriceChange? = null,
    val state: State = State.READY,
    val startColor: String? = null,
    val endColor: String? = null,
    val isSupported: Boolean = true
) {
    enum class State {
        READY, LOADING, UNINITIALIZED
    }

    val hasPricePerUnit: Boolean = fiatPricePerUnit != BigDecimal.ZERO
}

enum class PromptItem {
    FINGER_PRINT,
    PAPER_KEY,
    UPGRADE_PIN,
    RECOMMEND_RESCAN,
    RATE_APP,
    VERIFY_USER
}
