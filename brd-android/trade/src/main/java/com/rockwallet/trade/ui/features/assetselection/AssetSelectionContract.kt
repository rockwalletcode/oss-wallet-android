package com.rockwallet.trade.ui.features.assetselection

import com.rockwallet.common.ui.base.RockWalletContract

interface AssetSelectionContract {

    sealed class Event : RockWalletContract.Event {
        object BackClicked : Event()
        object LoadAssets : Event()
        data class SearchChanged(val query: String?) : Event()
        data class AssetSelected(val asset: AssetSelectionAdapter.AssetSelectionItem) : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        data class ShowToast(val message: String): Effect()
        data class Back(
            val requestKey: String,
            val selectedCurrency: String?
        ) : Effect()
    }

    data class State(
        val title: String,
        val search: String = "",
        val assets: List<AssetSelectionAdapter.AssetSelectionItem> = emptyList(),
        val adapterItems: List<AssetSelectionAdapter.AssetSelectionItem> = emptyList(),
        val initialLoadingVisible: Boolean = false
    ) : RockWalletContract.State
}