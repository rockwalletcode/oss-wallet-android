package com.breadwallet.util

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class FiatUiParserTest {

    private lateinit var currency: Currency

    @Before
    fun setUp() {
        Locale.setDefault(Locale.US)
        currency = Currency.getInstance(Locale.US)
    }

    @Test
    fun formatFiatForUiAdvanced_inputFourDecimals_noChange() {
        val amount = BigDecimal("0.0032")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.0032", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputBiggerThanOne_noChange() {
        val amount = BigDecimal("2.32")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "2.32", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputLotOfDecimals_returnOnlyLastFourDigits() {
        val amount = BigDecimal("0.003123323")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.003123", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputTwoDecimals_noChange() {
        val amount = BigDecimal("0.99")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.99", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputBiggerThanOneThreeDecimals_returnTwoDecimals() {
        val amount = BigDecimal("123312.752")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "123,312.75", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputLotOfDecimals_returnOnlyLastFourDigits2() {
        val amount = BigDecimal("0.0000006722134")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.0000006722", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputBiggerThanOneTwoDecimals_returnNoChange() {
        val amount = BigDecimal("17321.00")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "17,321.00", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputLotOfDecimals_returnOnlyLastFourDigits3() {
        val amount = BigDecimal("0.6722134")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.6722", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputTrailingZeroes_returnRemoveTrailingZeroes() {
        val amount = BigDecimal("0.098100")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.0981", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputZero_returnDefaultDecimals() {
        val amount = BigDecimal("0")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.00", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputBiggerThanOneLotDecimals_returnDefaultDecimals() {
        val amount = BigDecimal("11.6798336400")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "11.68", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputLotOfDecimals_returnOnlyLastFourDigits4() {
        val amount = BigDecimal("0.00000762512")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.000007625", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputLotOfDecimalsTrailingZeroes_returnRemoveTrailingZeroes() {
        val amount = BigDecimal("0.000120036787")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.00012", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputSmallerThanOneTwoDecimals_returnNoChange() {
        val amount = BigDecimal("0.01")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.01", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputSmallerThanOneTrailingZeroes_returnRemoveTrailingZeroes() {
        val amount = BigDecimal("0.10000")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.10", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputSmallerThanOneThreeDecimals_returnNoChange() {
        val amount = BigDecimal("0.234")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.234", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputBiggerThanOneTrailingZeroes_returnRemoveTrailingZeroes() {
        val amount = BigDecimal("6.90000")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "6.90", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputZero_returnDefaultDecimals2() {
        val amount = BigDecimal("0.0")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.00", actual)
    }

    @Test
    fun formatFiatForUiAdvanced_inputLotOfDecimals_returnNoChange() {
        val amount = BigDecimal("0.00000000937")
        val actual = amount.formatFiatForUiAdvanced(currency.currencyCode)
        Assert.assertEquals(currency.symbol + "0.00000000937", actual)
    }
}