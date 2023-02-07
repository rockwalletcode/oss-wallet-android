package com.breadwallet.breadbox

import android.content.Context
import com.breadwallet.ext.addUniqueHeader
import com.breadwallet.tools.manager.BRSharedPrefs
import com.rockwallet.common.data.RockWalletApiConstants
import com.rockwallet.registration.utils.UserSessionManager
import com.platform.tools.SessionHolder
import kotlinx.coroutines.CoroutineScope
import okhttp3.Interceptor
import okhttp3.Response

class RockWalletAuthInterceptor(
    private val context: Context,
    private val scope: CoroutineScope
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilderWithDeviceId = request
            .newBuilder()
            .addUniqueHeader(request, RockWalletApiConstants.HEADER_DEVICE_ID, BRSharedPrefs.getDeviceId())
            .addUniqueHeader(request, RockWalletApiConstants.HEADER_USER_AGENT, RockWalletApiConstants.USER_AGENT_VALUE)

        val requestUrl = chain.request().url.toString()
        if (!requestUrl.startsWith(RockWalletApiConstants.HOST_BLOCKSATOSHI_API)) {
            return chain.proceed(
                requestBuilderWithDeviceId.build()
            )
        }

        val response = requestBuilderWithDeviceId
            .addUniqueHeader(request, RockWalletApiConstants.HEADER_AUTHORIZATION, SessionHolder.getSessionKey())
            .build()
            .run(chain::proceed)


        UserSessionManager.checkIfSessionExpired(
            scope = scope,
            context = context,
            response = response
        )

        return response
    }
}