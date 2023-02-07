package com.rockwallet.kyc.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.rockwallet.common.data.enums.KycStatus
import com.rockwallet.kyc.R
import com.rockwallet.kyc.databinding.PartialVerificationStatusBinding

class AccountVerificationStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var callback: Callback? = null
    private val binding: PartialVerificationStatusBinding

    init {
        setBackgroundResource(R.drawable.bg_info_prompt)
        binding = PartialVerificationStatusBinding.inflate(
            LayoutInflater.from(context), this
        )

        with(binding) {
            btnProfileInfo.setOnClickListener {
                callback?.onButtonClicked(StatusButton.VERIFICATION_MORE_INFO)
            }

            btnVerifyAccount.setOnClickListener {
                callback?.onButtonClicked(StatusButton.VERIFY_ACCOUNT)
            }

            btnUpgradeLimits.setOnClickListener {
                callback?.onButtonClicked(StatusButton.UPGRADE_LIMITS)
            }

            btnDeclinedVerificationMoreInfo.setOnClickListener {
                callback?.onButtonClicked(StatusButton.VERIFICATION_DECLINED_INFO)
            }
        }
    }

    fun setStatus(status: KycStatus) {
        val state = mapToState(status)

        with(binding) {
            tvTitle.setText(state.title)
            tvSubtitle.setText(state.subtitle)

            tvStatus.isVisible = state.statusView != null
            btnVerifyAccount.isVisible = state.verifyAccountButtonVisible
            btnUpgradeLimits.isVisible = state.upgradeLimitsButtonVisible
            btnDeclinedVerificationMoreInfo.isVisible = state.verificationDeclinedButtonVisible

            // setup Status TextView
            state.statusView?.let {
                tvStatus.backgroundTintList = ContextCompat.getColorStateList(
                    context, state.statusView.backgroundTint
                )

                tvStatus.setText(it.text)
                tvStatus.setTextColor(
                    ContextCompat.getColor(
                        context, state.statusView.textColor
                    )
                )
            }
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    private fun mapToState(status: KycStatus) = when (status) {
        KycStatus.DEFAULT,
        KycStatus.EMAIL_VERIFIED,
        KycStatus.EMAIL_VERIFICATION_PENDING -> State.Default

        KycStatus.KYC1,
        KycStatus.KYC2_EXPIRED,
        KycStatus.KYC2_NOT_STARTED -> State.Level1Verified

        KycStatus.KYC2 -> State.Level2Verified
        KycStatus.KYC2_DECLINED -> State.Level2Declined
        KycStatus.KYC2_SUBMITTED -> State.Level2Pending
        KycStatus.KYC2_RESUBMISSION_REQUESTED -> State.Level2Resubmit
    }

    interface Callback {
        fun onButtonClicked(button: StatusButton)
    }

    sealed class StatusViewState(
        @StringRes val text: Int,
        @ColorRes val textColor: Int,
        @ColorRes val backgroundTint: Int
    ) {

        object Pending : StatusViewState(
            text = R.string.Account_Pending,
            textColor = R.color.light_contrast_01,
            backgroundTint = R.color.light_pending_1
        )

        object Verified : StatusViewState(
            text = R.string.Account_Verified,
            textColor = R.color.light_contrast_01,
            backgroundTint = R.color.light_success_1
        )

        object Resubmit : StatusViewState(
            text = R.string.Account_Resubmit,
            textColor = R.color.light_contrast_02,
            backgroundTint = R.color.light_error
        )

        object Declined : StatusViewState(
            text = R.string.Account_Declined,
            textColor = R.color.light_contrast_02,
            backgroundTint = R.color.light_error
        )
    }

    sealed class State(
        @StringRes val title: Int,
        @StringRes val subtitle: Int,
        val statusView: StatusViewState? = null,
        val upgradeLimitsButtonVisible: Boolean = false,
        val verifyAccountButtonVisible: Boolean = false,
        val verificationDeclinedButtonVisible: Boolean = false
    ) {
        object Default : State(
            title = R.string.Account_AccountLimits,
            subtitle = R.string.Account_FullAccess,
            verifyAccountButtonVisible = true
        )

        object Level1Verified : State(
            title = R.string.Account_AccountLimits,
            subtitle = R.string.Account_CurrentLimit,
            statusView = StatusViewState.Verified,
            upgradeLimitsButtonVisible = true
        )

        object Level2Pending : State(
            title = R.string.Account_AccountLimits,
            subtitle = R.string.Account_VerifiedAccountMessage,
            statusView = StatusViewState.Pending
        )

        object Level2Resubmit : State(
            title = R.string.Account_AccountLimits,
            subtitle = R.string.Account_DataIssues,
            statusView = StatusViewState.Resubmit,
            verificationDeclinedButtonVisible = true
        )

        object Level2Declined : State(
            title = R.string.Account_AccountLimits,
            subtitle = R.string.Account_DataIssues,
            statusView = StatusViewState.Declined,
            verificationDeclinedButtonVisible = true
        )

        object Level2Verified : State(
            title = R.string.Account_AccountLimits,
            subtitle = R.string.Account_SwapAndBuyLimit,
            statusView = StatusViewState.Verified
        )
    }

    enum class StatusButton {
        VERIFY_ACCOUNT,
        UPGRADE_LIMITS,
        VERIFICATION_MORE_INFO,
        VERIFICATION_DECLINED_INFO,
    }
}