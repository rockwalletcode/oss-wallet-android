package com.rockwallet.buy.ui.features.buydetails

import com.breadwallet.util.formatFiatForUi
import com.rockwallet.buy.data.enums.BuyDetailsFlow
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.common.utils.formatPercent
import com.rockwallet.trade.data.response.ExchangeOrder
import com.rockwallet.trade.data.response.ExchangeType
import java.math.BigDecimal
import java.math.RoundingMode

interface BuyDetailsContract {

    sealed class Event : RockWalletContract.Event {
        object LoadData : Event()
        object BackClicked : Event()
        object DismissClicked : Event()
        object OrderIdClicked : Event()
        object TransactionIdClicked : Event()
        object OnCreditInfoClicked : Event()
        object OnNetworkInfoClicked : Event()
    }

    sealed class Effect : RockWalletContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String): Effect()
        data class CopyToClipboard(val data: String) : Effect()
        data class ShowInfoDialog(
            val image: Int? = null,
            val title: Int,
            val description: Int
        ) : Effect()
    }

    sealed class State : RockWalletContract.State {
        object Loading : State()
        object Error : State()
        data class Loaded(
            val data: ExchangeOrder,
            val flow: BuyDetailsFlow
        ) : State() {
            val usdFee: BigDecimal
                get() = data.source.usdFee ?: BigDecimal.ZERO

            val networkFee: BigDecimal
                get() = data.destination.usdFee ?: BigDecimal.ZERO

            val cardFeePercent: String?
                get() = when (data.type) {
                    ExchangeType.BUY -> requireNotNull(data.source.feeRate).formatPercent()
                    else -> null
                }

            val achFeePercent: String?
                get() = when (data.type) {
                    ExchangeType.BUY_ACH -> String.format(
                        "%s + %s",
                        requireNotNull(data.source.feeFixedRate).formatFiatForUi(
                            currencyCode = data.source.currency,
                            showCurrencyName = false
                        ),
                        requireNotNull(data.source.feeRate).formatPercent()
                    )
                    else -> null
                }

            val purchasedAmount: BigDecimal
                get() = data.source.currencyAmount - (data.source.usdFee ?: BigDecimal.ZERO) - (data.destination.usdFee ?: BigDecimal.ZERO)

            val fiatPriceForOneCryptoUnit: BigDecimal
                get() = BigDecimal.ONE.divide(data.rate ?: BigDecimal.ONE, 20, RoundingMode.HALF_UP)
        }
    }
}