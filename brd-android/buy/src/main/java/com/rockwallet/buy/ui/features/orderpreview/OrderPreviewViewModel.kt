package com.rockwallet.buy.ui.features.orderpreview

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.rockwallet.buy.R
import com.rockwallet.buy.data.BuyApi
import com.rockwallet.common.data.RockWalletApiConstants
import com.rockwallet.common.data.Resource
import com.rockwallet.common.data.Status
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.common.utils.getString
import com.rockwallet.common.utils.toBundle
import com.rockwallet.trade.data.response.ExchangeType
import com.rockwallet.trade.ui.features.swap.SwapInputHelper
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.erased.instance

class OrderPreviewViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : RockWalletViewModel<OrderPreviewContract.State, OrderPreviewContract.Event, OrderPreviewContract.Effect>(
    application, savedStateHandle
), OrderPreviewEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    private val helper = SwapInputHelper(
        direct.instance(), direct.instance(), direct.instance()
    )

    private lateinit var arguments: OrderPreviewFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = OrderPreviewFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = OrderPreviewContract.State(
        fiatAmount = arguments.fiatAmount,
        networkFee = arguments.networkFee,
        fiatCurrency = arguments.fiatCurrency,
        quoteResponse = arguments.quoteResponse,
        cryptoCurrency = arguments.cryptoCurrency,
        paymentInstrument = arguments.paymentInstrument
    )

    override fun onBackClicked() {
        setEffect { OrderPreviewContract.Effect.Back }
    }

    override fun onDismissClicked() {
        setEffect { OrderPreviewContract.Effect.Dismiss }
    }

    override fun onConfirmClicked() {
        setEffect { OrderPreviewContract.Effect.RequestUserAuthentication }
    }

    override fun onCreditInfoClicked() {
        setEffect {
            OrderPreviewContract.Effect.ShowInfoDialog(
                title = R.string.Swap_CardFee,
                description = R.string.Buy_CardFee
            )
        }
    }

    override fun onNetworkInfoClicked() {
        setEffect {
            OrderPreviewContract.Effect.ShowInfoDialog(
                title = R.string.Buy_NetworkFees,
                description = R.string.Buy_NetworkFeeMessage
            )
        }
    }

    override fun onSecurityCodeInfoClicked() {
        setEffect {
            OrderPreviewContract.Effect.ShowInfoDialog(
                image = R.drawable.ic_info_cvv,
                title = R.string.Buy_SecurityCode,
                description = R.string.Buy_SecurityCodePopup
            )
        }
    }

    override fun onTermsAndConditionsClicked() {
        setEffect {
            OrderPreviewContract.Effect.OpenWebsite(
                RockWalletApiConstants.URL_TERMS_AND_CONDITIONS
            )
        }
    }

    override fun onUserAuthenticationSucceed() {
        val quoteResponse = requireNotNull(currentState.quoteResponse)
        if (quoteResponse.isExpired()) {
            setEffect { OrderPreviewContract.Effect.TimeoutScreen }
            return
        }

        callApi(
            endState = { copy(fullScreenLoadingIndicator = false) },
            startState = { copy(fullScreenLoadingIndicator = true) },
            action = {
                val destinationAddress =
                    helper.loadAddress(currentState.cryptoCurrency)?.toString() ?: return@callApi Resource.error(
                        message = getString(R.string.ErrorMessages_default)
                    )

                when (currentState.paymentInstrument) {
                    is PaymentInstrument.Card -> createCardOrder(
                        quoteId = quoteResponse.quoteId,
                        destinationAddress = destinationAddress,
                        card = currentState.paymentInstrument as PaymentInstrument.Card
                    )
                    is PaymentInstrument.BankAccount -> createAchOrder(
                        quoteId = quoteResponse.quoteId,
                        destinationAddress = destinationAddress,
                        account = currentState.paymentInstrument as PaymentInstrument.BankAccount
                    )
                }
            },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = requireNotNull(it.data)
                        val reference = response.paymentReference
                        val redirectUrl = response.redirectUrl

                        setEffect {
                            OrderPreviewContract.Effect.PaymentProcessing(
                                paymentReference = reference,
                                redirectUrl = redirectUrl,
                                paymentType = when (currentState.paymentInstrument) {
                                    is PaymentInstrument.Card -> ExchangeType.BUY
                                    is PaymentInstrument.BankAccount -> ExchangeType.BUY_ACH
                                }
                            )
                        }
                    }

                    Status.ERROR -> {
                        setState { copy(confirmButtonEnabled = false) }
                        setEffect {
                            if (getString(R.string.Swap_RequestTimedOut) == it.message) {
                                OrderPreviewContract.Effect.TimeoutScreen
                            } else {
                                OrderPreviewContract.Effect.ShowError(
                                    it.message ?: getString(R.string.ErrorMessages_default)
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    private suspend fun createCardOrder(destinationAddress: String, quoteId: String, card: PaymentInstrument.Card) = buyApi.createOrder(
        quoteId = quoteId,
        baseQuantity = currentState.totalFiatAmount,
        termQuantity = currentState.cryptoAmount,
        destination = destinationAddress,
        nologCvv = currentState.securityCode,
        sourceInstrumentId = card.id
    )

    private suspend fun createAchOrder(destinationAddress: String, quoteId: String, account: PaymentInstrument.BankAccount) = buyApi.createAchOrder(
        quoteId = quoteId,
        baseQuantity = currentState.totalFiatAmount,
        termQuantity = currentState.cryptoAmount,
        destination = destinationAddress,
        accountId = account.id
    )

    override fun onSecurityCodeChanged(securityCode: String) {
        setState { copy(securityCode = securityCode).validate() }
    }

    override fun onTermsChanged(accepted: Boolean) {
        setState { copy(termsAccepted = accepted).validate() }
    }

    private fun OrderPreviewContract.State.validate() = copy(
        confirmButtonEnabled = when (paymentInstrument) {
            is PaymentInstrument.Card -> securityCode.length == 3 && termsAccepted
            is PaymentInstrument.BankAccount -> termsAccepted
        }
    )
}