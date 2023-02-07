package com.breadwallet.ui.verifyaccount

import android.os.Bundle
import androidx.core.os.bundleOf
import com.breadwallet.R
import com.breadwallet.databinding.ControllerVerifyAccountBinding
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.verifyaccount.VerifyScreen.E
import com.breadwallet.ui.verifyaccount.VerifyScreen.F
import com.breadwallet.ui.verifyaccount.VerifyScreen.M
import com.rockwallet.common.utils.underline
import com.spotify.mobius.First
import com.spotify.mobius.Init
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.kodein.di.direct
import org.kodein.di.erased.instance

class VerifyController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args) {

    constructor(type: NavigationTarget.VerifyProfile.Type) : this(
        bundleOf(
            EXTRA_TYPE to type
        )
    )

    override val defaultModel = M()
    override val update = VerifyUpdate
    override val init = Init<M, F> { model ->
        First.first(
            model, setOf(F.LoadProfileData)
        )
    }

    override val flowEffectHandler
        get() = createVerifyScreenHandler(
            direct.instance()
        )

    private val type: NavigationTarget.VerifyProfile.Type = arg(EXTRA_TYPE)
    private val binding by viewBinding(ControllerVerifyAccountBinding::inflate)

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with(binding) {
            btnLater.underline()

            merge(
                btnVerifyAccount.clicks().map { E.OnVerifyClicked },
                btnLater.clicks().map { E.OnDismissClicked }
            )
        }
    }

    override fun M.render() {
        with(binding) {
            ifChanged(M::hasKyc1) {
                ivIcon.setImageResource(
                    if (type == NavigationTarget.VerifyProfile.Type.BUY) {
                        R.drawable.ic_upgrade_account
                    } else {
                        R.drawable.ic_verify_account
                    }
                )

                tvVerifySubtitle.setText(
                    when (type) {
                        NavigationTarget.VerifyProfile.Type.BUY -> if (hasKyc1 == true) {
                            R.string.VerifyAccount_Subtitle_Kyc1Buy
                        } else {
                            R.string.VerifyAccount_Subtitle_NoKycBuy
                        }
                        NavigationTarget.VerifyProfile.Type.SWAP ->
                            R.string.VerifyAccount_Subtitle_NoKycSwap
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_TYPE = "type"
    }
}