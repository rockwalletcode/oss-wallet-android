package com.rockwallet.trade.data

import android.content.Context
import com.rockwallet.common.data.RockWalletApiConstants
import com.rockwallet.common.data.Resource
import com.rockwallet.common.utils.RockWalletApiResponseMapper
import com.rockwallet.trade.R
import com.rockwallet.trade.data.model.SwapBuyTransactionData
import com.rockwallet.trade.data.request.CreateSwapOrderRequest
import com.rockwallet.trade.data.request.EstimateEthFeeRequest
import com.rockwallet.trade.data.response.CreateSwapOrderResponse
import com.rockwallet.trade.data.response.ExchangeOrder
import com.rockwallet.trade.data.response.QuoteResponse
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class SwapApi(
    private val context: Context,
    private val service: SwapService
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

    suspend fun getQuote(from: String, to: String): Resource<QuoteResponse?> {
        return try {
            val response = service.getQuote(from, to)
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun createOrder(baseQuantity: BigDecimal, termQuantity: BigDecimal, quoteId: String, destination: String): Resource<CreateSwapOrderResponse?> {
        return try {
            val response = service.createOrder(
                CreateSwapOrderRequest(
                    quoteId = quoteId,
                    destination = destination,
                    depositQuantity = baseQuantity,
                    withdrawQuantity = termQuantity
                )
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            val error: Resource<CreateSwapOrderResponse?> = responseMapper.mapError(
                context = context,
                exception = ex
            )

            if (error.message?.contains("expired quote", true) == true) {
                return Resource.error(
                    message = context.getString(R.string.Swap_RequestTimedOut)
                )
            } else {
                error
            }
        }
    }

    suspend fun getExchangeOrder(exchangeId: String): Resource<ExchangeOrder?> {
        return try {
            val response = service.getExchange(exchangeId)
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun getSwapTransactions(): Resource<List<SwapBuyTransactionData>?> {
        return try {
            val response = service.getExchanges()
            Resource.success(data = response.exchanges)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun estimateEthFee(amount: BigDecimal, currency: String, destination: String): Resource<BigDecimal?> {
        return try {
            val response = service.estimateEthFee(
                EstimateEthFeeRequest(
                    amount, currency, destination
                )
            )
            Resource.success(data = response.fee)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    companion object {

        fun create(context: Context, swapApiInterceptor: SwapApiInterceptor, moshiConverter: MoshiConverterFactory) = SwapApi(
            context = context,
            service = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(swapApiInterceptor)
                        .build()
                )
                .baseUrl(RockWalletApiConstants.HOST_SWAP_API)
                .addConverterFactory(moshiConverter)
                .build()
                .create(SwapService::class.java)
        )
    }
}
