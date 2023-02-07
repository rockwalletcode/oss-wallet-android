package com.rockwallet.kyc.ui.features.prevalidation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.kyc.R
import com.rockwallet.kyc.databinding.FragmentPreValidationBinding
import kotlinx.coroutines.flow.collect

class PreValidationFragment : Fragment(),
    RockWalletView<PreValidationContract.State, PreValidationContract.Effect> {

    private lateinit var binding: FragmentPreValidationBinding
    private val viewModel: PreValidationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pre_validation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreValidationBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(PreValidationContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(PreValidationContract.Event.DismissCLicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(PreValidationContract.Event.ConfirmClicked)
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
    }

    override fun render(state: PreValidationContract.State) {
        // empty
    }

    override fun handleEffect(effect: PreValidationContract.Effect) {
        when (effect) {
            is PreValidationContract.Effect.Back ->
                findNavController().popBackStack()

            is PreValidationContract.Effect.Dismiss ->
                findNavController().navigate(
                    PreValidationFragmentDirections.actionAccountVerification()
                )

            is PreValidationContract.Effect.ProofOfIdentity ->
                findNavController().navigate(
                    PreValidationFragmentDirections.actionProofOfIdentity()
                )
        }
    }
}