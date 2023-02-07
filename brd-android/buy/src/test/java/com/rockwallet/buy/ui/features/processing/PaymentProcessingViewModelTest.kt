package com.rockwallet.buy.ui.features.processing

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.rockwallet.buy.ui.features.TestRockWalletApplication
import com.rockwallet.common.ui.base.RockWalletContract
import com.rockwallet.common.ui.base.RockWalletViewModel
import com.rockwallet.trade.data.response.ExchangeType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestRockWalletApplication::class)
class PaymentProcessingViewModelTest {

    private lateinit var viewModel: PaymentProcessingViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        savedStateHandle = SavedStateHandle(
            PaymentProcessingFragmentArgs(
                TEST_PAYMENT_REFERENCE, ExchangeType.BUY, TEST_REDIRECT_URL
            ).toBundle().toMap()
        )

        viewModel = PaymentProcessingViewModel(
            RuntimeEnvironment.application, savedStateHandle
        )
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()
        Assert.assertEquals(TEST_PAYMENT_REFERENCE, actual.paymentReference)
    }

    @Test
    fun onBackToHomeClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onBackToHomeClicked() },
            targetEffect = PaymentProcessingContract.Effect.Dismiss
        )
    }

    @Test
    fun onContactSupportClicked_verifyEffect()  {
        viewModel.testEffect(
            action = { viewModel.onContactSupportClicked() },
            targetEffect = PaymentProcessingContract.Effect.ContactSupport
        )
    }

    @Test
    fun onTryDifferentMethodClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onTryDifferentMethodClicked() },
            targetEffect = PaymentProcessingContract.Effect.BackToBuy
        )
    }

    @Test
    fun onPurchaseDetailsClicked_verifyEffect() {
        viewModel.setState {
            PaymentProcessingContract.State.PaymentCompleted(
                TEST_PAYMENT_REFERENCE,
                ExchangeType.BUY
            )
        }

        viewModel.testEffect(
            action = { viewModel.onPurchaseDetailsClicked() },
            targetEffect = PaymentProcessingContract.Effect.GoToPurchaseDetails(TEST_PAYMENT_REFERENCE)
        )
    }

    companion object {
        private const val TEST_REDIRECT_URL = "http://localhost/"
        private const val TEST_PAYMENT_REFERENCE = "test_reference"
    }
}

fun RockWalletViewModel<*,*,*>.testEffect(
    action: () -> Unit, targetEffect: RockWalletContract.Effect
) = runBlocking {
    val actual = effect
        .onStart { action() }
        .firstOrNull()

    Assert.assertEquals(targetEffect, actual)
}

fun Bundle.toMap() = keySet().associateWith { get(it) }
