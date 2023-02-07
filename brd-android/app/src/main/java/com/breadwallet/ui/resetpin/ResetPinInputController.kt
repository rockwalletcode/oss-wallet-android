package com.breadwallet.ui.resetpin

import android.os.Bundle
import android.view.View
import com.breadwallet.R
import com.breadwallet.databinding.ControllerResetPinInputBinding
import com.breadwallet.legacy.presenter.customviews.PinLayout
import com.breadwallet.legacy.presenter.customviews.PinLayout.PinLayoutListener
import com.breadwallet.tools.animation.SpringAnimator
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.controllers.SignalController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.resetpin.ResetPinInput.E
import com.breadwallet.ui.resetpin.ResetPinInput.F
import com.breadwallet.ui.resetpin.ResetPinInput.M
import drewcarlson.mobius.flow.FlowTransformer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.kodein.di.direct
import org.kodein.di.erased.instance

class ResetPinInputController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args),
    SignalController.Listener {

    override val defaultModel = M.createDefault()
    override val update = ResetPinInputUpdate
    override val flowEffectHandler: FlowTransformer<F, E>
        get() = createInputPinHandler(direct.instance())

    private val binding by viewBinding(ControllerResetPinInputBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        binding.keyboard.setShowDecimal(false)
    }

    override fun handleViewEffect(effect: ViewEffect) {
        when (effect) {
            F.ErrorShake -> SpringAnimator.failShakeAnimation(applicationContext, binding.pinDigits)
            F.ShowPinError -> toastLong(R.string.UpdatePin_setPinError)
            F.ResetPin -> binding.pinDigits.resetPin()
        }
    }

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return merge(
            binding.btnBack.clicks().map { E.OnBackClicked },
            binding.btnFaq.clicks().map { E.OnFaqClicked },
            binding.pinDigits.bindInput()
        )
    }

    override fun onSignalComplete() {
        eventConsumer.accept(E.OnBreadSignalShown)
    }

    private fun PinLayout.bindInput() = callbackFlow<E> {
        val channel = channel
        setup(binding.keyboard, false, object : PinLayoutListener {
            override fun onInvalidPinInserted(pin: String, attemptsLeft: Int) {
                channel.offer(E.OnPinEntered(pin, false))
            }

            override fun onValidPinInserted(pin: String) {
                channel.offer(E.OnPinEntered(pin, true))
            }

            override fun onPinLocked() {
                channel.offer(E.OnPinLocked)
            }
        })
        awaitClose { cleanUp() }
    }

    override fun M.render() {
        ifChanged(M::mode) {
            binding.tvSubtitle.setText(
                when (mode) {
                    M.Mode.NEW -> R.string.ResetPin_subtitleNewPin
                    M.Mode.CONFIRM -> R.string.ResetPin_subtitleConfirmNewPin
                }
            )

//            binding.tvAdvice.visibility = when(mode) {
//                M.Mode. -> View.INVISIBLE
//                else -> View.VISIBLE
//            }
        }
    }
}
