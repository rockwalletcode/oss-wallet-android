/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 8/6/19.
 * Copyright (c) 2019 breadwallet LLC
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
package com.breadwallet.model

import androidx.annotation.ColorRes
import com.breadwallet.appcore.R
import java.util.Locale
import kotlin.math.absoluteValue

/**
 * Price change of a currency over the last 24Hrs.
 */
data class PriceChange (val changePercentage24Hrs: Double,
                        val change24Hrs: Double) {

    private val prefix : String = when {
        change24Hrs > 0 -> "+"
        change24Hrs < 0 -> "-"
        else -> ""
    }

    override fun toString(): String {
        val amount = String.format(Locale.getDefault(), "%.2f", change24Hrs.absoluteValue)
        val percentage = String.format(Locale.getDefault(), "%.2f", changePercentage24Hrs.absoluteValue)
        return "$prefix$percentage% ($amount)"
    }

    fun getPercentageChange(): String {
        val percentage = String.format(Locale.getDefault(), "%.2f", changePercentage24Hrs.absoluteValue)
        return "$prefix$percentage%"
    }

    @ColorRes
    fun getChangeColor() : Int = when {
        change24Hrs > 0 -> R.color.rock_wallet_green
        change24Hrs < 0 -> R.color.rock_wallet_red
        else -> R.color.rock_wallet_shuttle_grey
    }
}
