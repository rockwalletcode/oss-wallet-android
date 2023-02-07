package com.rockwallet.buy.ui.features.buydetails

import androidx.lifecycle.SavedStateHandle
import com.rockwallet.buy.data.enums.BuyDetailsFlow
import com.rockwallet.buy.ui.features.TestRockWalletApplication
import com.rockwallet.buy.ui.features.processing.testEffect
import com.rockwallet.buy.ui.features.processing.toMap
import com.rockwallet.trade.data.response.ExchangeOrder
import com.rockwallet.trade.data.response.ExchangeOrderStatus
import com.rockwallet.trade.data.response.ExchangeSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
@Config(application = TestRockWalletApplication::class)
class BuyDetailsViewModelTest {

    private lateinit var viewModel: BuyDetailsViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    private val exchangeOrder = ExchangeOrder(
        orderId = CURRENT_EXCHANGE_ID,
        status = ExchangeOrderStatus.PENDING,
        timestamp = System.currentTimeMillis(),
        source = ExchangeSource(
            currency = "USD",
            usdAmount = BigDecimal.TEN,
            currencyAmount = BigDecimal.TEN,
            transactionId = null,
            paymentInstrument = null,
            usdFee = null,
            feeRate = null,
            feeFixedRate = null
        ),
        destination = ExchangeSource(
            currency = "BSV",
            usdAmount = null,
            currencyAmount = BigDecimal.ONE,
            transactionId = CURRENT_TRANSACTION_ID,
            paymentInstrument = null,
            usdFee = null,
            feeRate = null,
            feeFixedRate = null
        ),
        rate = null,
        type = null,
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle(
            BuyDetailsFragmentArgs(CURRENT_EXCHANGE_ID, CURRENT_FLOW).toBundle().toMap()
        )

        viewModel = BuyDetailsViewModel(RuntimeEnvironment.application, savedStateHandle)
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()
        Assert.assertEquals(BuyDetailsContract.State.Loading, actual)
    }

    @Test
    fun onBackClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onBackClicked() },
            targetEffect = BuyDetailsContract.Effect.Dismiss
        )
    }

    @Test
    fun onDismissClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onDismissClicked() },
            targetEffect = BuyDetailsContract.Effect.Dismiss
        )
    }

    @Test
    fun onOrderIdClicked_verifyEffect() {
        viewModel.setState {
            BuyDetailsContract.State.Loaded(
                exchangeOrder, CURRENT_FLOW
            )
        }

        viewModel.testEffect(
            action = { viewModel.onOrderIdClicked() },
            targetEffect = BuyDetailsContract.Effect.CopyToClipboard(CURRENT_EXCHANGE_ID)
        )
    }

    @Test
    fun onTransactionIdClicked_verifyEffect() {
        viewModel.setState {
            BuyDetailsContract.State.Loaded(
                exchangeOrder, CURRENT_FLOW
            )
        }

        viewModel.testEffect(
            action = { viewModel.onTransactionIdClicked() },
            targetEffect = BuyDetailsContract.Effect.CopyToClipboard(CURRENT_TRANSACTION_ID)
        )
    }

    companion object {
        private val CURRENT_FLOW = BuyDetailsFlow.PURCHASE
        private const val CURRENT_EXCHANGE_ID = "text_exchange_id"
        private const val CURRENT_TRANSACTION_ID = "text_transaction_id"
    }
}