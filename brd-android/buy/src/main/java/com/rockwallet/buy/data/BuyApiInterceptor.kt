package com.rockwallet.buy.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import com.rockwallet.trade.data.SwapApiInterceptor

class BuyApiInterceptor(
    context: Context, scope: CoroutineScope
) : SwapApiInterceptor(context, scope)