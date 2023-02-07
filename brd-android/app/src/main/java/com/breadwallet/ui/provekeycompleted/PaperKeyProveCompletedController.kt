package com.breadwallet.ui.provekeycompleted

import android.os.Bundle
import androidx.core.os.bundleOf
import com.breadwallet.databinding.ControllerPaperKeyProveCompletedBinding
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.navigation.OnCompleteAction
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.E
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.F
import com.breadwallet.ui.provekeycompleted.PaperKeyProveCompleted.M
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val EXTRA_ON_COMPLETE = "on-complete"

class PaperKeyProveCompletedController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args) {

    constructor(onComplete: OnCompleteAction) : this(
        bundleOf(EXTRA_ON_COMPLETE to onComplete.name)
    )

    private val onComplete = OnCompleteAction.valueOf(arg(EXTRA_ON_COMPLETE))

    override val defaultModel = M.createDefault(onComplete)
    override val update = PaperKeyProveCompletedUpdate

    override val flowEffectHandler
        get() = createProveKeyCompletedScreenHandler()

    private val binding by viewBinding(ControllerPaperKeyProveCompletedBinding::inflate)

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with(binding) {
            btnToWallet.clicks().map { E.OnGoToWalletClicked }
        }
    }
}