package com.rockwallet.registration.ui.features.enteremail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.breadwallet.tools.security.BrdUserManager
import com.rockwallet.common.data.Status
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.getString
import com.rockwallet.common.utils.validators.EmailValidator
import com.rockwallet.registration.R
import com.rockwallet.registration.data.RegistrationApi
import com.rockwallet.registration.utils.RegistrationUtils
import com.platform.tools.SessionHolder
import com.platform.tools.SessionState
import com.platform.tools.TokenHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class RegistrationEnterEmailViewModel(
    application: Application,
) : RockWalletViewModel<RegistrationEnterEmailContract.State, RegistrationEnterEmailContract.Event, RegistrationEnterEmailContract.Effect>(
    application
), RegistrationEnterEmailEventHandler, KodeinAware {

    override val kodein by closestKodein { application }
    private val registrationApi by instance<RegistrationApi>()
    private val registrationUtils by instance<RegistrationUtils>()
    private val userManager by instance<BrdUserManager>()

    override fun createInitialState() = RegistrationEnterEmailContract.State()

    override fun onDismissClicked() {
        setEffect { RegistrationEnterEmailContract.Effect.Dismiss }
    }

    override fun onEmailChanged(email: String) {
        setState { copy(email = email).validate() }
    }

    override fun onPromotionsClicked(isChecked: Boolean) {
        setState { copy(promotionsEnabled = isChecked) }
    }

    override fun onNextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = TokenHolder.retrieveToken()
            if (token.isNullOrBlank()) {
                setEffect {
                    RegistrationEnterEmailContract.Effect.ShowToast(
                        getString(R.string.ErrorMessages_default)
                    )
                }
                return@launch
            }

            callApi(
                endState = { copy(loadingVisible = false) },
                startState = { copy(loadingVisible = true) },
                action = {
                    registrationApi.associateEmail(
                        email = currentState.email,
                        token = token,
                        subscribe = currentState.promotionsEnabled,
                        headers = registrationUtils.getAssociateRequestHeaders(
                            salt = currentState.email,
                            token = token
                        )
                    )
                },
                callback = {
                    when (it.status) {
                        Status.SUCCESS -> {
                            userManager.updateVerifyPrompt(true)
                            SessionHolder.updateSession(
                                sessionKey = it.data!!.sessionKey,
                                state = SessionState.CREATED
                            )

                            setEffect {
                                RegistrationEnterEmailContract.Effect.GoToVerifyEmail(
                                    currentState.email
                                )
                            }
                        }

                        Status.ERROR ->
                            setEffect {
                                RegistrationEnterEmailContract.Effect.ShowToast(
                                    it.message ?: getString(R.string.ErrorMessages_default)
                                )
                            }
                    }
                }
            )
        }
    }

    private fun RegistrationEnterEmailContract.State.validate() = copy(
        nextEnabled = EmailValidator(email)
    )
}