/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 3/04/21.
 * Copyright (c) 2021 breadwallet LLC
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
package com.brd.prefs

import com.brd.concurrent.freeze
import com.brd.util.uuid

internal expect val platformKeyMap: Map<String, String>

class BrdPreferences(
    private val preferences: Preferences,
) {
    companion object {
        internal const val KEY_DEVICE_ID = "cosmos_device_id"
        internal const val KEY_USER_FIAT = "cosmos_user_fiat"

        private const val KEY_COUNTRY_CODE = "cosmos_country_code"
        private const val KEY_REGION_CODE = "cosmos_region_code"
        private const val KEY_PREVIOUSLY_PURCHASED_CURRENCY =
            "cosmos_previously_purchased_currency"
    }

    init {
        freeze()
    }

    var deviceId: String
        get() = preferences.getString(platformKey(KEY_DEVICE_ID), uuid())
        set(value) {
            preferences.putString(platformKey(KEY_DEVICE_ID), value)
        }

    var fiatCurrencyCode: String
        get() = preferences.getString(platformKey(KEY_USER_FIAT))
        set(value) {
            preferences.putString(platformKey(KEY_USER_FIAT), value)
        }

    /**
     * The currency code of the user's latest crypto purchase or "btc".
     */
    var previouslyPurchasedCurrency: String
        get() = preferences.getString(KEY_PREVIOUSLY_PURCHASED_CURRENCY, "btc")
        set(value) {
            preferences.putString(KEY_PREVIOUSLY_PURCHASED_CURRENCY, value)
        }

    var countryCode: String?
        get() = preferences.getStringOrNull(KEY_COUNTRY_CODE)
        set(value) {
            if (value.isNullOrBlank()) {
                preferences.remove(KEY_COUNTRY_CODE)
            } else {
                preferences.putString(KEY_COUNTRY_CODE, value)
            }
        }

    var regionCode: String?
        get() = preferences.getStringOrNull(KEY_REGION_CODE)
        set(value) {
            if (value.isNullOrBlank()) {
                preferences.remove(KEY_REGION_CODE)
            } else {
                preferences.putString(KEY_REGION_CODE, value)
            }
        }

    private fun platformKey(key: String): String = platformKeyMap[key] ?: key
}
