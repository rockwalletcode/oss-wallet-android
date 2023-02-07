package com.rockwallet.trade.ui.features.failed

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
import com.rockwallet.trade.databinding.FragmentSwapFailedBinding
import kotlinx.coroutines.flow.collect

class SwapFailedFragment : Fragment(),
    RockWalletView<SwapFailedContract.State, SwapFailedContract.Effect> {

    private val viewModel: SwapFailedViewModel by viewModels()

    private lateinit var binding: FragmentSwapFailedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_swap_failed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSwapFailedBinding.bind(view)

        with(binding) {
            btnGoHome.setOnClickListener {
                viewModel.setEvent(SwapFailedContract.Event.GoHomeClicked)
            }

            btnSwapAgain.underline()
            btnSwapAgain.setOnClickListener {
                viewModel.setEvent(SwapFailedContract.Event.SwapAgainClicked)
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
            //no back button
        }
    }

    override fun render(state: SwapFailedContract.State) {
        //empty
    }

    override fun handleEffect(effect: SwapFailedContract.Effect) {
        when (effect) {
            SwapFailedContract.Effect.Back ->
                findNavController().popBackStack()

            SwapFailedContract.Effect.Dismiss ->
                requireActivity().finish()
        }
    }
}