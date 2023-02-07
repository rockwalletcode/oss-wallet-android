package com.rockwallet.buy.ui.features.timeout

import com.rockwallet.buy.ui.features.TestRockWalletApplication
import com.rockwallet.buy.ui.features.processing.testEffect
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestRockWalletApplication::class)
class PaymentTimeoutViewModelTest {

    private lateinit var viewModel: PaymentTimeoutViewModel

    @Before
    fun setUp() {
        viewModel = PaymentTimeoutViewModel(RuntimeEnvironment.application)
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()
        Assert.assertEquals(PaymentTimeoutContract.State, actual)
    }

    @Test
    fun onTryAgainClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onTryAgainClicked() },
            targetEffect = PaymentTimeoutContract.Effect.BackToBuy
        )
    }
}