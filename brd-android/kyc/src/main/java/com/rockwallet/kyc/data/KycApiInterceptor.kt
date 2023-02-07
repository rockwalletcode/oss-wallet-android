package com.rockwallet.kyc.data

import com.breadwallet.ext.addUniqueHeader
import com.breadwallet.tools.manager.BRSharedPrefs
import com.rockwallet.common.data.RockWalletApiConstants
import com.platform.tools.SessionHolder
import okhttp3.Interceptor
import okhttp3.Response

class KycApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) : Response {
        val request = chain.request()
        return request
            .newBuilder()
            .addUniqueHeader(request, RockWalletApiConstants.HEADER_DEVICE_ID, BRSharedPrefs.getDeviceId())
            .addUniqueHeader(request, RockWalletApiConstants.HEADER_USER_AGENT, RockWalletApiConstants.USER_AGENT_VALUE)
            .addUniqueHeader(request, RockWalletApiConstants.HEADER_AUTHORIZATION, SessionHolder.getSessionKey())
            .build()
            .run(chain::proceed)
    }
}