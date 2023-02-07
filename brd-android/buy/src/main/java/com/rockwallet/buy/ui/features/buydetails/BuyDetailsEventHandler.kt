package com.rockwallet.buy.ui.features.buydetails

import com.rockwallet.common.ui.base.RockWalletEventHandler

interface BuyDetailsEventHandler: RockWalletEventHandler<BuyDetailsContract.Event> {

    override fun handleEvent(event: BuyDetailsContract.Event) {
        return when (event) {
            is BuyDetailsContract.Event.LoadData -> onLoadData()
            is BuyDetailsContract.Event.BackClicked -> onBackClicked()
            is BuyDetailsContract.Event.DismissClicked -> onDismissClicked()
            is BuyDetailsContract.Event.OrderIdClicked -> onOrderIdClicked()
            is BuyDetailsContract.Event.TransactionIdClicked -> onTransactionIdClicked()
            is BuyDetailsContract.Event.OnCreditInfoClicked -> onCreditInfoClicked()
            is BuyDetailsContract.Event.OnNetworkInfoClicked -> onNetworkInfoClicked()
        }
    }

    fun onLoadData()

    fun onBackClicked()

    fun onDismissClicked()

    fun onOrderIdClicked()

    fun onTransactionIdClicked()

    fun onCreditInfoClicked()

    fun onNetworkInfoClicked()
}