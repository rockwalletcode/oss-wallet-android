/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 10/17/19.
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
package com.breadwallet.ui.settings

import android.net.Uri
import com.breadwallet.R
import com.breadwallet.tools.util.Link
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.util.CurrencyCode
import com.plaid.link.configuration.LinkTokenConfiguration
import com.rockwallet.common.ui.dialog.RockWalletGenericDialogArgs
import dev.zacsweers.redacted.annotations.Redacted

object SettingsScreen {

    const val CONFIRM_EXPORT_TRANSACTIONS_DIALOG = "confirm_export"
    const val CONFIRM_EXPORT_TRANSACTIONS_DIALOG_POSITIVE = "confirm_export_positive"
    const val CONFIRM_EXPORT_TRANSACTIONS_DIALOG_NEGATIVE = "confirm_export_negative"
    const val TEST_ROK_WALLET_DIALOG = "TEST_ROK_WALLET_DIALOG"
    const val TEST_ROK_WALLET_DIALOG_POSITIVE = "TEST_ROK_WALLET_DIALOG_POSITIVE"
    const val TEST_ROK_WALLET_DIALOG_NEGATIVE = "TEST_ROK_WALLET_DIALOG_NEGATIVE"

    data class M(
        val section: SettingsSection,
        @Redacted val items: List<SettingsItem> = listOf(),
        val isLoading: Boolean = false
    ) {
        companion object {
            fun createDefault(section: SettingsSection) = M(section)
        }
    }

    sealed class E {

        data class OnLinkScanned(val link: Link) : E()
        data class OnOptionClicked(val option: SettingsOption) : E()

        data class OnOptionsLoaded(@Redacted val options: List<SettingsItem>) : E()

        object OnBackClicked : E()
        object OnCloseClicked : E()

        object OnAuthenticated : E()

        data class ShowPhrase(@Redacted val phrase: List<String>) : E()
        data class SetApiServer(val host: String) : E()
        data class SetPlatformDebugUrl(val url: String) : E()
        data class SetPlatformBundle(val bundle: String) : E()
        data class SetTokenBundle(val bundle: String) : E()
        object OnWalletsUpdated : E()
        object ShowHiddenOptions : E()
        object OnCloseHiddenMenu : E()
        data class OnTestGenericDialogResult(val message: String) : E()

        data class OnATMMapClicked(val url: String, val mapJson: String) : E()

        object OnExportTransactionsConfirmed : E()
        data class OnPlaidLinkGenerated(val link: String) : E()
        data class OnTransactionsExportFileGenerated(val uri: Uri) : E()
    }

    sealed class F {
        object SendAtmFinderRequest : F()
        object RequestPlaidLink : F()
        object SendLogs : F()
        object ViewLogs : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.LogcatViewer
        }
        object ViewMetadata : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.MetadataViewer
        }
        object ShowApiServerDialog : F(), ViewEffect
        object ShowPlatformDebugUrlDialog : F(), ViewEffect
        object ShowPlatformBundleDialog : F(), ViewEffect
        object ShowTokenBundleDialog : F(), ViewEffect
        object ResetDefaultCurrencies : F()
        object WipeNoPrompt : F()
        object GetPaperKey : F()
        object EnableAllWallets : F()
        object ClearBlockchainData : F()
        object ToggleRateAppPrompt : F()
        object RefreshTokens : F()
        object DetailedLogging : F()
        object CopyPaperKey : F()

        data class SetApiServer(val host: String) : F()
        data class SetPlatformDebugUrl(val url: String) : F()
        data class SetPlatformBundle(val bundle: String) : F()
        data class SetTokenBundle(val bundle: String) : F()
        data class LoadOptions(val section: SettingsSection) : F()

        data class GoToSection(val section: SettingsSection) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Menu(section)
        }

        object GoBack : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Back
        }

        object GoToSupport : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.SupportPage("")
        }
        
        object GoToFeedback : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToFeedback
        }

        object GoToQrScan : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.QRScanner
        }

        object GoToGooglePlay : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.ReviewBrd
        }

        object GoToAbout : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.About
        }

        object GoToDisplayCurrency : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.DisplayCurrency
        }

        object GoToNotificationsSettings : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.NotificationsSettings
        }

        object GoToShareData : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.ShareDataSettings
        }

        object GoToImportWallet : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.ImportWallet()
        }

        data class GoToSyncBlockchain(
            val currencyCode: CurrencyCode
        ) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.SyncBlockchain(currencyCode)
        }

        object GoToNodeSelector : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.BitcoinNodeSelector
        }

        object GoToEnableSegWit : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.EnableSegWit
        }

        object GoToLegacyAddress : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.LegacyAddress
        }

        object GoToFingerprintAuth : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.FingerprintSettings
        }

        object GoToUpdatePin : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.SetPin()
        }

        object GoToWipeWallet : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.WipeWallet
        }

        object GoToDeleteAccount : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.DeleteAccount
        }

        object GoToOnboarding : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.OnBoarding
        }

        object GoToNativeApiExplorer : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.NativeApiExplorer
        }

        object GoToHomeScreen : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Home
        }

        object GoToAuthentication : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Authentication()
        }

        data class GoToPaperKey(
            @Redacted val phrase: List<String>
        ) : F(), NavigationEffect {
            override val navigationTarget =
                NavigationTarget.PaperKey(phrase, null)
        }

        data class GoToFastSync(
            val currencyCode: CurrencyCode
        ) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.FastSync(currencyCode)
        }

        data class GoToLink(val link: Link) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.DeepLink(
                link = link,
                authenticated = true
            )
        }

        data class GoToATMMap(val url: String, val mapJson: String) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.ATMMap(url, mapJson)
        }

        object RelaunchHomeScreen : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Home
        }

        data class ShowToast(val message: String) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.RockWalletToast(
                type = NavigationTarget.RockWalletToast.Type.INFO,
                message = message
            )
        }

        data class OpenPlaid(val configuration: LinkTokenConfiguration) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.OpenPlaid(configuration)
        }

        object TestRokWalletGenericDialog : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.RockWalletGenericDialog(
                args = RockWalletGenericDialogArgs(
                    title = "Test title",
                    description = "Test description",
                    positive = RockWalletGenericDialogArgs.ButtonData(
                        icon = R.drawable.fingerprint_icon,
                        title = "Test positive button",
                        resultKey = TEST_ROK_WALLET_DIALOG_POSITIVE
                    ),
                    negative = RockWalletGenericDialogArgs.ButtonData(
                        title = "Test negative button",
                        resultKey = TEST_ROK_WALLET_DIALOG_NEGATIVE
                    ),
                    requestKey = TEST_ROK_WALLET_DIALOG
                )
            )
        }

        object ThrowCrash : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.ThrowCrash("Test crash was triggered")
        }


        object ShowConfirmExportTransactions : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.RockWalletGenericDialog(
                RockWalletGenericDialogArgs(
                    requestKey = CONFIRM_EXPORT_TRANSACTIONS_DIALOG,
                    titleRes = R.string.ExportConfirmation_title,
                    descriptionRes = R.string.ExportConfirmation_message,
                    positive = RockWalletGenericDialogArgs.ButtonData(
                        resultKey = CONFIRM_EXPORT_TRANSACTIONS_DIALOG_POSITIVE,
                        titleRes = R.string.ExportConfirmation_continue,
                    ),
                    negative = RockWalletGenericDialogArgs.ButtonData(
                        resultKey = CONFIRM_EXPORT_TRANSACTIONS_DIALOG_NEGATIVE,
                        titleRes = R.string.ExportConfirmation_cancel
                    )
                )
            )
        }

        object GenerateTransactionsExportFile: F()
        data class ExportTransactions(val uri: Uri) : F(), ViewEffect
    }
}
