package com.rockwallet.common.data

import android.os.Build
import android.util.Xml
import com.rockwallet.common.BuildConfig
import java.net.URLEncoder

object RockWalletApiConstants {

    const val HOST = "${BuildConfig.SERVER_HOST}/blocksatoshi"

    private const val BASE_URL = "https://$HOST"

    const val HOST_KYC_API = "$BASE_URL/one/kyc/"
    const val HOST_SWAP_API = "$BASE_URL/exchange/"
    const val HOST_AUTH_API = "$BASE_URL/one/auth/"
    const val HOST_WALLET_API = "$BASE_URL/wallet"
    const val HOST_BLOCKSATOSHI_API = "$BASE_URL/blocksatoshi"

    const val ENDPOINT_CURRENCIES = "$HOST_WALLET_API/currencies"
    const val ENDPOINT_FIAT_CURRENCIES = "$HOST_WALLET_API/fiat_currencies"

    const val URL_SUPPORT_PAGE = "https://help.rockwallet.com/"
    const val URL_PRIVACY_POLICY = "https://uploads-ssl.webflow.com/636012218b483e2e5e98b3e4/636919229c53fbb688353ef3_RockWallet-USPrivacy-2022-07.pdf"
    const val URL_TERMS_AND_CONDITIONS = "https://uploads-ssl.webflow.com/636012218b483e2e5e98b3e4/636919221303a5dc76ddc0ae_RockWallet-TCs-2022Oct.pdf"

    const val HEADER_DEVICE_ID = "X-Device-ID"
    const val HEADER_USER_AGENT = "User-agent"
    const val HEADER_AUTHORIZATION = "Authorization"

    private const val SYSTEM_PROPERTY_USER_AGENT = "http.agent"
    private const val UA_APP_NAME = "rockwallet/"
    private const val UA_PLATFORM = "android/"

    val USER_AGENT_VALUE = getUserAgent()

    fun getGoogleDocsEmbeddablePDFViewer(pdfUrl:String) : String {
        val urlEncoded = URLEncoder.encode(pdfUrl, Xml.Encoding.UTF_8.toString())
        return "https://docs.google.com/gview?embedded=true&url=$urlEncoded"
    }

    private fun getUserAgent(): String {
        val deviceUserAgent = (System.getProperty(SYSTEM_PROPERTY_USER_AGENT) ?: "")
            .split("\\s".toRegex())
            .firstOrNull()

        //example: rockwallet/100000 Dalvik/2.1.0 android/12
        return "${UA_APP_NAME}${BuildConfig.VERSION_CODE} $deviceUserAgent ${UA_PLATFORM}${Build.VERSION.RELEASE}"
    }
}