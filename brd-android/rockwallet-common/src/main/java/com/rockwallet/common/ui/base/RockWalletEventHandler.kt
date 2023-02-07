package com.rockwallet.common.ui.base

interface RockWalletEventHandler<Event> {
    fun handleEvent(event: Event)
}