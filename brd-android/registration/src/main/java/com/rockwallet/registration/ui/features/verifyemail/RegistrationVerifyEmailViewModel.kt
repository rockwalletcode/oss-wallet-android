package com.rockwallet.registration.ui.features.verifyemail

import android.app.Application
import android.graphics.Typeface
import android.text.style.StyleSpan
import androidx.core.text.toSpannable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.security.ProfileManager
import com.rockwallet.common.data.Status
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.getString
import com.rockwallet.common.utils.toBundle
import com.rockwallet.common.utils.validators.ConfirmationCodeValidator
import com.rockwallet.registration.R
import com.rockwallet.registration.data.RegistrationApi
import com.rockwallet.registration.ui.RegistrationFlow
import com.rockwallet.registration.ui.RegistrationActivity
import com.platform.tools.SessionHolder
import com.platform.tools.SessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class RegistrationVerifyEmailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<RegistrationVerifyEmailContract.State, RegistrationVerifyEmailContract.Event, RegistrationVerifyEmailContract.Effect>(
    application, savedStateHandle
), RegistrationVerifyEmailEventHandler, KodeinAware {

    override val kodein by closestKodein { application }
    private val registrationApi by instance<RegistrationApi>()

    private lateinit var arguments: RegistrationVerifyEmailFragmentArgs
    private val userManager by instance<BrdUserManager>()
    private val profileManager by instance<ProfileManager>()

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = RegistrationVerifyEmailFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = RegistrationVerifyEmailContract.State(
        subtitle = createSubtitle(),
        changeEmailButtonVisible = arguments.flow != RegistrationFlow.RE_VERIFY
    )

    override fun onDismissClicked() {
        setEffect { RegistrationVerifyEmailContract.Effect.Dismiss() }
    }

    override fun onChangeEmailClicked() {
        setEffect { RegistrationVerifyEmailContract.Effect.Back }
    }

    override fun onCodeChanged(code: String) {
        setState {
            copy(
                code = code,
                codeErrorVisible = false
            ).validate()
        }
    }

    override fun onConfirmClicked() {
        val currentSession = SessionHolder.getSession()
        if (currentSession.isDefaultSession()) {
            setEffect {
                RegistrationVerifyEmailContract.Effect.ShowToast(
                    getString(R.string.ErrorMessages_default)
                )
            }
            return
        }

        callApi(
            endState = { copy(loadingVisible = false) },
            startState = { copy(loadingVisible = true) },
            action = { registrationApi.associateAccountConfirm(currentState.code) },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        userManager.updateVerifyPrompt(false)
                        profileManager.updateProfile()

                        SessionHolder.updateSession(
                            sessionKey = currentSession.key,
                            state = SessionState.VERIFIED
                        )

                        showCompletedState()
                    }

                    Status.ERROR ->
                        setState { copy(codeErrorVisible = true).validate() }
                }
            }
        )
    }

    private fun showCompletedState() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(completedViewVisible = true) }
            delay(1000)
            setState { copy(completedViewVisible = false) }
            setEffect {
                RegistrationVerifyEmailContract.Effect.Dismiss(
                    RegistrationActivity.RESULT_VERIFIED
                )
            }
        }
    }

    override fun onResendEmailClicked() {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { registrationApi.resendAssociateAccountChallenge() },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setEffect {
                            RegistrationVerifyEmailContract.Effect.ShowToast(
                                getString(R.string.AccountCreation_CodeSent)
                            )
                        }

                    Status.ERROR -> {
                        //empty
                    }
                }
            }
        )
    }

    private fun createSubtitle(): CharSequence {
        val email = arguments.email
        val fullText = getString(R.string.AccountCreation_EnterCode, email)
        val startIndex = fullText.indexOf(email)
        val spannable = fullText.toSpannable()
        spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, startIndex + email.length, 0)
        return spannable
    }

    private fun RegistrationVerifyEmailContract.State.validate() =
        copy(confirmEnabled = ConfirmationCodeValidator(code) && !codeErrorVisible)
}