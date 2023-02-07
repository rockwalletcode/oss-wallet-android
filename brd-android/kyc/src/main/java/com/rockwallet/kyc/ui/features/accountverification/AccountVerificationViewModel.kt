package com.rockwallet.kyc.ui.features.accountverification

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.breadwallet.tools.security.ProfileManager
import com.rockwallet.common.data.enums.KycStatus
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.getString
import com.rockwallet.kyc.R
import com.rockwallet.kyc.ui.customview.AccountVerificationStatusView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class AccountVerificationViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) :
    RockWalletViewModel<AccountVerificationContract.State, AccountVerificationContract.Event, AccountVerificationContract.Effect>(
        application, savedStateHandle
    ), AccountVerificationEventHandler, KodeinAware {

    override val kodein by closestKodein { application }
    private val profileManager by kodein.instance<ProfileManager>()

    init {
        subscribeProfileChanges()
    }

    override fun createInitialState() = AccountVerificationContract.State.Empty()

    override fun onBackClicked() {
        setEffect { AccountVerificationContract.Effect.Back }
    }

    override fun onDismissClicked() {
        setEffect { AccountVerificationContract.Effect.Dismiss }
    }

    override fun onInfoClicked() {
        setEffect { AccountVerificationContract.Effect.Info }
    }

    override fun onLevel1Clicked() {
        if (currentState is AccountVerificationContract.State.Content) {
            navigateOnLevel1Clicked()
        }
    }

    override fun onLevel2Clicked() {
        if (currentState is AccountVerificationContract.State.Content) {
            navigateOnLevel2Clicked()
        }
    }
    fun updateProfile() {
        profileManager.updateProfile()
    }

    private fun subscribeProfileChanges() {
        profileManager.profileChanges()
            .onEach { profile ->
                if (profile == null) {
                    setEffect {
                        AccountVerificationContract.Effect.ShowToast(
                            getString(R.string.ErrorMessages_default)
                        )
                    }
                    setState { AccountVerificationContract.State.Empty(false) }
                    return@onEach
                }

                setState {
                    AccountVerificationContract.State.Content(
                        profile = profile,
                        level1State = mapStatusToLevel1State(profile.kycStatus),
                        level2State = mapStatusToLevel2State(
                            profile.kycStatus,
                            profile.kycFailureReason
                        )
                    )
                }
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    private fun navigateOnLevel1Clicked() {
        val state = currentState as AccountVerificationContract.State.Content
        setEffect {
            when (state.profile.kycStatus) {
                KycStatus.DEFAULT,
                KycStatus.EMAIL_VERIFIED,
                KycStatus.EMAIL_VERIFICATION_PENDING,
                KycStatus.KYC1,
                KycStatus.KYC2_EXPIRED,
                KycStatus.KYC2_DECLINED,
                KycStatus.KYC2_NOT_STARTED,
                KycStatus.KYC2_RESUBMISSION_REQUESTED ->
                    AccountVerificationContract.Effect.GoToKycLevel1

                KycStatus.KYC2_SUBMITTED ->
                    AccountVerificationContract.Effect.ShowToast(
                        getString(R.string.AccountKYCLevelTwo_VerificationPending)
                    )

                KycStatus.KYC2 ->
                    AccountVerificationContract.Effect.ShowLevel1ChangeConfirmationDialog
            }
        }
    }

    private fun navigateOnLevel2Clicked() {
        val state = currentState as AccountVerificationContract.State.Content
        setEffect {
            when (state.profile.kycStatus) {
                KycStatus.DEFAULT,
                KycStatus.EMAIL_VERIFIED,
                KycStatus.EMAIL_VERIFICATION_PENDING ->
                    AccountVerificationContract.Effect.ShowToast(
                        getString(R.string.AccountKYCLevelTwo_CompleteLevelOneFirst)
                    )


                KycStatus.KYC1,
                KycStatus.KYC2_EXPIRED,
                KycStatus.KYC2_DECLINED,
                KycStatus.KYC2_NOT_STARTED,
                KycStatus.KYC2_RESUBMISSION_REQUESTED ->
                    AccountVerificationContract.Effect.GoToKycLevel2

                KycStatus.KYC2_SUBMITTED ->
                    AccountVerificationContract.Effect.ShowToast(
                        getString(R.string.AccountKYCLevelTwo_VerificationPending)
                    )

                KycStatus.KYC2 ->
                    AccountVerificationContract.Effect.ShowToast(
                        getString(R.string.AccountKYCLevelTwo_updateLevelOne)
                    )
            }
        }
    }

    private fun mapStatusToLevel1State(status: KycStatus): AccountVerificationContract.Level1State {
        val state = when (status) {
            KycStatus.DEFAULT,
            KycStatus.EMAIL_VERIFIED,
            KycStatus.EMAIL_VERIFICATION_PENDING -> null
            else -> AccountVerificationStatusView.StatusViewState.Verified
        }

        return AccountVerificationContract.Level1State(
            isEnabled = true,
            statusState = state
        )
    }

    private fun mapStatusToLevel2State(status: KycStatus, kycFailureReason: String?): AccountVerificationContract.Level2State {
        return when (status) {
            KycStatus.DEFAULT,
            KycStatus.EMAIL_VERIFIED,
            KycStatus.EMAIL_VERIFICATION_PENDING ->
                AccountVerificationContract.Level2State(
                    isEnabled = false,
                    statusState = null
                )

            KycStatus.KYC1,
            KycStatus.KYC2_EXPIRED,
            KycStatus.KYC2_NOT_STARTED -> AccountVerificationContract.Level2State(
                isEnabled = true
            )

            KycStatus.KYC2_DECLINED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Declined,
                verificationError = kycFailureReason ?: getString(R.string.ErrorMessages_default)
            )

            KycStatus.KYC2_RESUBMISSION_REQUESTED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Resubmit,
                verificationError = kycFailureReason ?: getString(R.string.ErrorMessages_default)
            )

            KycStatus.KYC2_SUBMITTED -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Pending
            )
            KycStatus.KYC2 -> AccountVerificationContract.Level2State(
                isEnabled = true,
                statusState = AccountVerificationStatusView.StatusViewState.Verified
            )
        }
    }
}