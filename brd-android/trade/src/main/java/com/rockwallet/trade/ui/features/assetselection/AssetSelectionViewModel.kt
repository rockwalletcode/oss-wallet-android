package com.rockwallet.trade.ui.features.assetselection

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.getString
import com.rockwallet.common.utils.toBundle
import com.rockwallet.trade.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.erased.instance

class AssetSelectionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<AssetSelectionContract.State, AssetSelectionContract.Event, AssetSelectionContract.Effect>(
    application, savedStateHandle
), AssetSelectionEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val handler = AssetSelectionHandler(
        direct.instance(), direct.instance(), direct.instance()
    )

    private lateinit var arguments: AssetSelectionFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = AssetSelectionFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = AssetSelectionContract.State(
        title = arguments.title
    )

    override fun onBackClicked() {
        setEffect {
            AssetSelectionContract.Effect.Back(
                requestKey = arguments.requestKey,
                selectedCurrency = null
            )
        }
    }

    override fun onSearchChanged(query: String?) {
        setState { copy(search = query ?: "") }
        applyFilters()
    }

    override fun onAssetSelected(asset: AssetSelectionAdapter.AssetSelectionItem) {
        setEffect {
            if (asset.enabled) {
                AssetSelectionContract.Effect.Back(
                    requestKey = arguments.requestKey,
                    selectedCurrency = asset.cryptoCurrencyCode
                )
            } else {
                AssetSelectionContract.Effect.ShowToast(
                    getString(R.string.Swap_enableAssetFirst)
                )
            }
        }
    }

    override fun onLoadAssets() {
        viewModelScope.launch(Dispatchers.IO) {
            val assets = handler.getAssets(
                supportedCurrencies = arguments.currencies,
                sourceCurrency = arguments.sourceCurrency
            )

            setState { copy(assets = assets) }
            applyFilters()
        }
    }

    private fun applyFilters() {
        setState {
            copy(
                adapterItems = currentState.assets.filter {
                    it.title.contains(currentState.search, true) ||
                            it.subtitle.contains(currentState.search, true)
                }
            )
        }
    }
}