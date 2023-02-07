package com.rockwallet.kyc.ui.features.stateselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.RockWalletToastUtil
import com.rockwallet.kyc.R
import com.rockwallet.kyc.databinding.FragmentStateSelectionBinding
import kotlinx.coroutines.flow.collect

class StateSelectionFragment : Fragment(),
    RockWalletView<StateSelectionContract.State, StateSelectionContract.Effect> {

    private lateinit var binding: FragmentStateSelectionBinding
    private val viewModel: StateSelectionViewModel by viewModels()

    private val adapter = StateSelectionAdapter {
        viewModel.setEvent(
            StateSelectionContract.Event.StateSelected(it)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_state_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStateSelectionBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(StateSelectionContract.Event.BackClicked)
            }

            etSearch.doAfterTextChanged {
                viewModel.setEvent(StateSelectionContract.Event.SearchChanged(it?.toString()))
            }

            val layoutManager = LinearLayoutManager(context)
            rvStates.adapter = adapter
            rvStates.layoutManager = layoutManager
            rvStates.setHasFixedSize(true)
            rvStates.addItemDecoration(
                DividerItemDecoration(
                    context, layoutManager.orientation
                )
            )
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

    override fun render(state: StateSelectionContract.State) {
        adapter.submitList(state.adapterItems)
    }

    override fun handleEffect(effect: StateSelectionContract.Effect) {
        when (effect) {
            is StateSelectionContract.Effect.Back -> {
                val bundle = bundleOf(
                    EXTRA_SELECTED_STATE to effect.selectedState
                )
                parentFragmentManager.setFragmentResult(effect.requestKey, bundle)
                findNavController().popBackStack()
            }

            is StateSelectionContract.Effect.ShowToast ->
                RockWalletToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )
        }
    }

    companion object {
        const val EXTRA_SELECTED_STATE = "selected_state_item"
    }
}