package com.rockwallet.trade.ui.features.assetselection

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface AssetSelectionEventHandler: RockWalletEventHandler<AssetSelectionContract.Event> {

    override fun handleEvent(event: AssetSelectionContract.Event) {
        return when (event) {
            is AssetSelectionContract.Event.LoadAssets -> onLoadAssets()
            is AssetSelectionContract.Event.BackClicked -> onBackClicked()
            is AssetSelectionContract.Event.SearchChanged -> onSearchChanged(event.query)
            is AssetSelectionContract.Event.AssetSelected -> onAssetSelected(event.asset)
        }
    }

    fun onBackClicked()

    fun onLoadAssets()

    fun onSearchChanged(query: String?)

    fun onAssetSelected(asset: AssetSelectionAdapter.AssetSelectionItem)
}