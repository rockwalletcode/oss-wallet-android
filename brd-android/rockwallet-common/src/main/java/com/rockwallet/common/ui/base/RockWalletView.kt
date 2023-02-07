package com.rockwallet.common.ui.base

interface RockWalletView<State: RockWalletContract.State, Effect: RockWalletContract.Effect> {
    fun render(state: State)
    fun handleEffect(effect: Effect)
}