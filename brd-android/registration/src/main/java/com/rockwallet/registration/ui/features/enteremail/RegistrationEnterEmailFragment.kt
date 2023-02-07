package com.rockwallet.registration.ui.features.enteremail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.RockWalletToastUtil
import com.rockwallet.common.utils.hideKeyboard
import com.rockwallet.common.utils.showKeyboard
import com.rockwallet.common.utils.textOrEmpty
import com.rockwallet.registration.R
import com.rockwallet.registration.databinding.FragmentRegistrationEnterEmailBinding
import com.rockwallet.registration.ui.RegistrationFlow
import kotlinx.coroutines.flow.collect

class RegistrationEnterEmailFragment : Fragment(),
    RockWalletView<RegistrationEnterEmailContract.State, RegistrationEnterEmailContract.Effect> {

    private lateinit var binding: FragmentRegistrationEnterEmailBinding
    private val viewModel: RegistrationEnterEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_registration_enter_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegistrationEnterEmailBinding.bind(view)

        with(binding) {
            btnNext.setOnClickListener {
                etEmail.hideKeyboard()
                viewModel.setEvent(RegistrationEnterEmailContract.Event.NextClicked)
            }

            btnDismiss.setOnClickListener {
                etEmail.hideKeyboard()
                viewModel.setEvent(RegistrationEnterEmailContract.Event.DismissClicked)
            }

            etEmail.showKeyboard()
            etEmail.doAfterTextChanged {
                viewModel.setEvent(
                    RegistrationEnterEmailContract.Event.EmailChanged(
                        it.textOrEmpty()
                    )
                )
            }
            cbPromotions.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.setEvent(RegistrationEnterEmailContract.Event.PromotionsClicked(isChecked))
            }

        }

        // collect UI state
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                render(it)
            }
        }

        // collect UI effect
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                handleEffect(it)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            //User shouldn't be allowed to go back
        }
    }

    override fun render(state: RegistrationEnterEmailContract.State) {
        with(binding) {
            btnNext.isEnabled = state.nextEnabled
            loadingView.root.isVisible = state.loadingVisible
        }
    }

    override fun handleEffect(effect: RegistrationEnterEmailContract.Effect) {
        when (effect) {
            is RegistrationEnterEmailContract.Effect.Dismiss ->
                requireActivity().finish()

            is RegistrationEnterEmailContract.Effect.ShowToast ->
                RockWalletToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )

            is RegistrationEnterEmailContract.Effect.GoToVerifyEmail ->
                findNavController().navigate(
                    RegistrationEnterEmailFragmentDirections.actionVerifyEmail(
                        email = effect.email,
                        flow = RegistrationFlow.REGISTER
                    )
                )
        }
    }
}