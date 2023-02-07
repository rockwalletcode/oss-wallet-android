package com.rockwallet.buy.data

import android.content.Context
import com.breadwallet.tools.security.ProfileManager
import com.rockwallet.buy.data.request.PostPlaidErrorRequest
import com.rockwallet.buy.data.request.PostPlaidEventRequest
import com.plaid.link.event.LinkEvent
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import com.rockwallet.buy.R
import com.rockwallet.buy.data.enums.PaymentStatus
import com.rockwallet.buy.data.request.AddPaymentInstrumentRequest
import com.rockwallet.buy.data.request.CreateBuyAchOrderRequest
import com.rockwallet.buy.data.request.CreateBuyOrderRequest
import com.rockwallet.buy.data.request.PostPlaidTokenRequest
import com.rockwallet.buy.data.response.AddPaymentInstrumentResponse
import com.rockwallet.buy.data.response.CreateBuyOrderResponse
import com.rockwallet.buy.data.response.PaymentStatusResponse
import com.rockwallet.buy.data.response.PlaidLinkResponse
import com.rockwallet.common.data.Resource
import com.rockwallet.common.data.RockWalletApiConstants
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.utils.RockWalletApiResponseMapper
import com.rockwallet.trade.data.enums.QuoteType
import com.rockwallet.trade.data.response.QuoteResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class BuyApi(
    private val context: Context,
    private val service: BuyService,
    private val profileManager: ProfileManager
) {

    private val responseMapper = RockWalletApiResponseMapper()

    suspend fun getSupportedCurrencies(): Resource<List<String>?> {
        return try {
            val response = service.getSupportedCurrencies()
            Resource.success(data = response.currencies)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun addPaymentInstrument(
        token: String, firstName: String, lastName: String, city: String, state: String?,
        zip: String, countryCode: String, address: String
    ): Resource<AddPaymentInstrumentResponse?> {
        return try {
            val response = service.addPaymentInstrument(
                AddPaymentInstrumentRequest(
                    zip = zip,
                    city = city,
                    state = state,
                    token = token,
                    address = address,
                    lastName = lastName,
                    firstName = firstName,
                    countryCode = countryCode,
                )
            )

            Resource.success(data = response)
        } catch (ex: Exception) {
            val error: Resource<AddPaymentInstrumentResponse?> = responseMapper.mapError(
                context = context,
                exception = ex
            )

            //todo: refactor in future
            if (error.message?.contains("Access denied", true) == true) {
                profileManager.updateProfile()
                return Resource.error(
                    message = context.getString(R.string.ErrorMessages_Kyc2AccessDenied)
                )
            } else {
                error
            }
        }
    }

    suspend fun getPaymentInstruments(): Resource<List<PaymentInstrument>?> {
        return try {
            val response = service.getPaymentInstruments()
            Resource.success(data = response.paymentInstruments)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun deletePaymentInstrument(paymentInstrument: PaymentInstrument.Card): Resource<ResponseBody?> {
        return try {
            service.deletePaymentInstrument(paymentInstrument.id)
            Resource.success(data = null)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun getPaymentStatus(reference: String): Resource<PaymentStatusResponse?> {
        return try {
            val response = service.getPaymentStatus(reference)
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun getQuote(from: String, to: String, type: QuoteType): Resource<QuoteResponse?> {
        return try {
            val response = service.getQuote(from, to, type)
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun createOrder(baseQuantity: BigDecimal, termQuantity: BigDecimal, quoteId: String, destination: String, sourceInstrumentId: String, nologCvv: String): Resource<CreateBuyOrderResponse?> {
        return try {
            val response = service.createOrder(
                CreateBuyOrderRequest(
                    quoteId = quoteId,
                    destination = destination,
                    depositQuantity = baseQuantity,
                    withdrawQuantity = termQuantity,
                    sourceInstrumentId = sourceInstrumentId,
                    nologCvv = nologCvv
                )
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            handleCreateOrderError(ex)
        }
    }

    suspend fun createAchOrder(baseQuantity: BigDecimal, termQuantity: BigDecimal, quoteId: String, destination: String, accountId: String): Resource<CreateBuyOrderResponse?> {
        return try {
            val response = service.createAchOrder(
                CreateBuyAchOrderRequest(
                    quoteId = quoteId,
                    destination = destination,
                    depositQuantity = baseQuantity,
                    withdrawQuantity = termQuantity,
                    accountId = accountId
                )
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            handleCreateOrderError(ex)
        }
    }

    suspend fun getPlaidLink(): Resource<PlaidLinkResponse?> {
        return try {
            val response = service.getPlaidLinkToken()
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun postPlaidToken(plaidResult: LinkSuccess): Resource<Response<Unit>?> {
        return try {
            val response = service.postPlaidToken(
                PostPlaidTokenRequest(
                    token = plaidResult.publicToken,
                    mask = plaidResult.metadata.accounts.firstOrNull()?.mask
                )
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun postPlaidEvent(event: LinkEvent): Resource<Response<Unit>?> {
        return try {
            val response = service.postPlaidEvent(
                PostPlaidEventRequest.from(event)
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }
    suspend fun postPlaidError(plaidResult: LinkExit): Resource<Response<Unit>?> {
        return try {
            val response = service.postPlaidError(
                PostPlaidErrorRequest.from(plaidResult)
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    private fun handleCreateOrderError(ex: Exception): Resource<CreateBuyOrderResponse?> {
        val error: Resource<CreateBuyOrderResponse?> = responseMapper.mapError(
            context = context,
            exception = ex
        )

        //todo: refactor in future
        return if (error.message?.contains("expired quote", true) == true) {
            Resource.error(
                message = context.getString(R.string.Swap_RequestTimedOut)
            )
        } else if (error.message?.contains("Access denied", true) == true) {
            profileManager.updateProfile()
            Resource.error(
                message = context.getString(R.string.ErrorMessages_Kyc2AccessDenied)
            )
        } else {
            error
        }
    }

    companion object {

        fun create(context: Context, buyApiInterceptor: BuyApiInterceptor, moshiConverter: MoshiConverterFactory, profileManager: ProfileManager) =
            BuyApi(
                context = context,
                service = Retrofit.Builder()
                    .client(
                        OkHttpClient.Builder()
                            .readTimeout(30, TimeUnit.SECONDS)
                            .callTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .addInterceptor(buyApiInterceptor)
                            .build()
                    )
                    .baseUrl(RockWalletApiConstants.HOST_SWAP_API)
                    .addConverterFactory(moshiConverter)
                    .build()
                    .create(BuyService::class.java),
                profileManager = profileManager
            )
    }
}