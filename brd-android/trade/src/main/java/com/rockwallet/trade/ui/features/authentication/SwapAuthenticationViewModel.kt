package com.rockwallet.trade.ui.features.authentication

import android.app.Application
import androidx.biometric.BiometricPrompt
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.security.isFingerPrintAvailableAndSetup
import com.rockwallet.common.ui.base.RockWalletViewModel

class SwapAuthenticationViewModel(
    application: Application
) : RockWalletViewModel<SwapAuthenticationContract.State, SwapAuthenticationContract.Event, SwapAuthenticationContract.Effect>(
    application
), SwapAuthenticationEventHandler {

    override fun createInitialState(): SwapAuthenticationContract.State {
        /*val isFingerprintEnabled = isFingerprintEnabled()
        val authMode by lazy {
            val biometricManager = BiometricManager.from(getApplication())
            val hasBiometrics =
                biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            if (isFingerprintEnabled && hasBiometrics == BiometricManager.BIOMETRIC_SUCCESS) {
                SwapAuthenticationContract.AuthMode.USER_PREFERRED
            } else {
                SwapAuthenticationContract.AuthMode.PIN_REQUIRED
            }
        }*/

        return SwapAuthenticationContract.State(
            isFingerprintEnabled = false/*isFingerprintEnabled*/,
            authMode = SwapAuthenticationContract.AuthMode.PIN_REQUIRED /*authMode*/
        )
    }

    override fun onDismissClicked() {
        setEffect { SwapAuthenticationContract.Effect.Back(RESULT_KEY_CANCELED) }
    }

    override fun onPinValidated(valid: Boolean) {
        setEffect {
            when (valid) {
                true -> SwapAuthenticationContract.Effect.Back(RESULT_KEY_SUCCESS)
                false -> SwapAuthenticationContract.Effect.ShakeError
            }
        }
    }

    override fun onAuthFailed(errorCode: Int) {
        when (currentState.authMode) {
            SwapAuthenticationContract.AuthMode.USER_PREFERRED ->
                setState {
                    copy(authMode = SwapAuthenticationContract.AuthMode.PIN_REQUIRED)
                }
            else ->
                setEffect {
                    SwapAuthenticationContract.Effect.Back(
                        if (errorCode == BiometricPrompt.ERROR_CANCELED) {
                            RESULT_KEY_CANCELED
                        } else {
                            RESULT_KEY_FAILURE
                        }
                    )
                }
        }
    }

    override fun onAuthSucceeded() {
        setEffect { SwapAuthenticationContract.Effect.Back(RESULT_KEY_SUCCESS) }
    }

    private fun isFingerprintEnabled(): Boolean {
        return isFingerPrintAvailableAndSetup(getApplication())
                && BRSharedPrefs.sendMoneyWithFingerprint
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val REQUEST_KEY = "req_key_swap_auth"
        const val RESULT_KEY_SUCCESS = "res_key_swap_auth_success"
        const val RESULT_KEY_FAILURE = "res_key_swap_auth_failure"
        const val RESULT_KEY_CANCELED = "res_key_swap_auth_canceled"
    }
}