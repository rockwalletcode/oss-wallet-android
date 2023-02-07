package com.rockwallet.trade.ui.features.processing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.underline
import com.rockwallet.trade.R
import com.rockwallet.trade.databinding.FragmentSwapProcessBinding
import kotlinx.coroutines.flow.collect

class SwapProcessingFragment : Fragment(),
    RockWalletView<SwapProcessingContract.State, SwapProcessingContract.Effect> {
    private val viewModel: SwapProcessingViewModel by viewModels()

    private lateinit var binding: FragmentSwapProcessBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swap_process, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSwapProcessBinding.bind(view)

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
            viewModel.setEvent(
               SwapProcessingContract.Event.DismissClicked
            )
        }
    }

    override fun render(state: SwapProcessingContract.State) {
        with(binding) {
            tvProcessingTitle.text = getString(
                R.string.Swap_Swapping, state.originCurrency.uppercase(), state.destinationCurrency.uppercase()
            )
            tvProcessingBody.text = getString(
                R.string.Swap_SwapStatus, state.destinationCurrency.uppercase()
            )

            btnHome.setOnClickListener {
                viewModel.setEvent(
                    SwapProcessingContract.Event.GoHomeClicked
                )
            }

            btnDetails.underline()
            btnDetails.setOnClickListener {
                viewModel.setEvent(
                    SwapProcessingContract.Event.OpenSwapDetails
                )
            }
        }
    }

    override fun handleEffect(effect: SwapProcessingContract.Effect) {
        when (effect) {
            SwapProcessingContract.Effect.Dismiss ->
                requireActivity().finish()
            SwapProcessingContract.Effect.GoHome ->
                requireActivity().finish()
            is SwapProcessingContract.Effect.OpenDetails ->
                findNavController().navigate(
                    SwapProcessingFragmentDirections.actionSwapDetails(effect.id)
                )
        }
    }
}