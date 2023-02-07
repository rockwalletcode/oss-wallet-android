package com.rockwallet.buy.ui.features.plaiderror

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rockwallet.buy.R
import com.rockwallet.buy.databinding.FragmentPlaidConnectionErrorBinding
import com.rockwallet.common.data.RockWalletApiConstants
import com.rockwallet.common.ui.base.RockWalletView
import com.rockwallet.common.utils.underline
import kotlinx.coroutines.flow.collect

class PlaidConnectionErrorFragment : Fragment(),
    RockWalletView<PlaidConnectionErrorContract.State, PlaidConnectionErrorContract.Effect> {

    private lateinit var binding: FragmentPlaidConnectionErrorBinding
    private val viewModel: PlaidConnectionErrorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_plaid_connection_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlaidConnectionErrorBinding.bind(view)

        with(binding) {
            btnTryAgain.setOnClickListener {
                viewModel.setEvent(PlaidConnectionErrorContract.Event.TryAgainClicked)
            }

            btnContactSupport.underline()
            btnContactSupport.setOnClickListener {
                viewModel.setEvent(PlaidConnectionErrorContract.Event.ContactSupportClicked)
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

    override fun render(state: PlaidConnectionErrorContract.State) {
        // empty
    }

    override fun handleEffect(effect: PlaidConnectionErrorContract.Effect) {
        when (effect) {
            PlaidConnectionErrorContract.Effect.BackToBuy -> {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(RESULT_KEY to RESULT_TRY_AGAIN)
                )

                findNavController().popBackStack(
                    R.id.fragmentBuyInput, false
                )
            }

            PlaidConnectionErrorContract.Effect.ContactSupport -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(RockWalletApiConstants.URL_SUPPORT_PAGE))
                startActivity(intent)
            }
        }
    }

    companion object {
        const val REQUEST_KEY = "requestPaymentTimeout"
        const val RESULT_KEY = "result"
        const val RESULT_TRY_AGAIN = "resultTryAgain"
    }
}