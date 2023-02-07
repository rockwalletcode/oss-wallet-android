/**
 * BreadWallet
 *
 * Created by Ahsan Butt <ahsan.butt@breadwallet.com> on 12/11/20.
 * Copyright (c) 2020 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.util

import com.breadwallet.logger.logError
import com.breadwallet.tools.util.BRConstants
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun BigDecimal.formatFiatForUi(currencyCode: String, scale: Int? = null, showCurrencySymbol: Boolean = true, showCurrencyName: Boolean = false): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()) as DecimalFormat
    val decimalFormatSymbols = currencyFormat.decimalFormatSymbols
    currencyFormat.isGroupingUsed = true
    currencyFormat.roundingMode = BRConstants.ROUNDING_MODE
    try {
        val currency = java.util.Currency.getInstance(currencyCode)
        val symbol = currency.symbol
        decimalFormatSymbols.currencySymbol = if (showCurrencySymbol) symbol else ""
        currencyFormat.decimalFormatSymbols = decimalFormatSymbols
        currencyFormat.negativePrefix = "-$symbol"
        currencyFormat.maximumFractionDigits = scale ?: currency.defaultFractionDigits
        currencyFormat.minimumFractionDigits = scale ?: currency.defaultFractionDigits
    } catch (e: IllegalArgumentException) {
        logError("Illegal Currency code: $currencyCode")
    }

    if (showCurrencyName) {
        return "${currencyFormat.format(this)} ${currencyCode.uppercase()}"
    }
    return currencyFormat.format(this)
}

fun BigDecimal.formatFiatForUiAdvanced(currencyCode: String, scale: Int? = null, showCurrencySymbol: Boolean = true, showCurrencyName: Boolean = false): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()) as DecimalFormat
    val decimalFormatSymbols = currencyFormat.decimalFormatSymbols
    currencyFormat.isGroupingUsed = true
    currencyFormat.roundingMode = BRConstants.ROUNDING_MODE
    try {
        val currency = java.util.Currency.getInstance(currencyCode)
        val symbol = currency.symbol
        decimalFormatSymbols.currencySymbol = if (showCurrencySymbol) symbol else ""
        currencyFormat.decimalFormatSymbols = decimalFormatSymbols
        currencyFormat.negativePrefix = "-$symbol"
        val bdSplit = this.toPlainString().split(".")

        if (this < BigDecimal.ONE && bdSplit.size == 2) {
            var decimalPart = bdSplit[1]
            val leadingZeroes =
                decimalPart.length - Regex("^0+").replaceFirst(decimalPart, "").length

            decimalPart = decimalPart.substring(
                0, leadingZeroes + kotlin.math.min(decimalPart.length - leadingZeroes,4))
            decimalPart = Regex("0+$").replace(decimalPart, "")

            currencyFormat.maximumFractionDigits =
                kotlin.math.max(decimalPart.length, currency.defaultFractionDigits)
            currencyFormat.minimumFractionDigits =
                kotlin.math.max(decimalPart.length, currency.defaultFractionDigits)
        } else {
            currencyFormat.maximumFractionDigits = currency.defaultFractionDigits
            currencyFormat.minimumFractionDigits = currency.defaultFractionDigits
        }

    } catch (e: IllegalArgumentException) {
        logError("Illegal Currency code: $currencyCode")
    }

    if (showCurrencyName) {
        return "${currencyFormat.format(this)} ${currencyCode.uppercase()}"
    }
    return currencyFormat.format(this)
}

fun String.withParentheses() = "($this)"