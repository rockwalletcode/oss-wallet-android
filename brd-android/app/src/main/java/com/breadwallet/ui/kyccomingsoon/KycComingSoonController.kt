package com.breadwallet.ui.kyccomingsoon

import android.os.Bundle
import com.breadwallet.databinding.ControllerKycComingSoonBinding
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.E
import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.F
import com.breadwallet.ui.kyccomingsoon.KycComingSoonScreen.M
import com.rockwallet.common.utils.underline
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class KycComingSoonController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args) {

    override val defaultModel = M
    override val update = KycComingSoonUpdate

    override val flowEffectHandler
        get() = createKycComingSoonScreenHandler()

    private val binding by viewBinding(ControllerKycComingSoonBinding::inflate)

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with(binding) {
            btnSupport.underline()

            merge(
                btnHome.clicks().map { E.OnBackToHomeClicked },
                btnSupport.clicks().map { E.OnContactSupportClicked }
            )
        }
    }
}