package com.rockwallet.buy.utils

import com.rockwallet.buy.R

class PlaidResponseCodeMapper {

    operator fun invoke(responseCode: String) = when(responseCode) {
        CODE_ACCOUNT_CLOSED -> R.string.ErrorMessages_Ach_AccountClosed
        CODE_ACCOUNT_FROZEN -> R.string.ErrorMessages_Ach_AccountFrozen
        CODE_INSUFFICIENT_FUNDS -> R.string.ErrorMessages_Ach_InsufficientFunds
        else -> R.string.ErrorMessages_Ach_ErrorWhileProcessing
    }

    private companion object {
        const val CODE_ACCOUNT_CLOSED = "30046"
        const val CODE_ACCOUNT_FROZEN = "30R16"
        const val CODE_INSUFFICIENT_FUNDS = "20051"
    }
}