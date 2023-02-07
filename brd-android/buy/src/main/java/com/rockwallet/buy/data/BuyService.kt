package com.rockwallet.buy.data

import com.rockwallet.buy.data.request.PostPlaidErrorRequest
import com.rockwallet.buy.data.request.PostPlaidEventRequest
import com.rockwallet.buy.data.request.AddPaymentInstrumentRequest
import com.rockwallet.buy.data.request.CreateBuyAchOrderRequest
import com.rockwallet.buy.data.request.CreateBuyOrderRequest
import com.rockwallet.buy.data.request.PostPlaidTokenRequest
import com.rockwallet.buy.data.response.*
import com.rockwallet.trade.data.enums.QuoteType
import com.rockwallet.trade.data.response.QuoteResponse
import com.rockwallet.trade.data.response.SupportedCurrenciesResponse
import retrofit2.Response
import retrofit2.http.*

interface BuyService {

    @GET("supported-currencies")
    suspend fun getSupportedCurrencies(): SupportedCurrenciesResponse

    @POST("payment-instrument")
    suspend fun addPaymentInstrument(
        @Body request: AddPaymentInstrumentRequest
    ): AddPaymentInstrumentResponse

    @GET("v2/payment-instruments")
    suspend fun getPaymentInstruments(): PaymentInstrumentsResponse

    @DELETE("payment-instrument")
    suspend fun deletePaymentInstrument(@Query("instrument_id") instrumentId: String): Response<Unit>

    @GET("payment-status")
    suspend fun getPaymentStatus(
        @Query("reference") reference: String
    ): PaymentStatusResponse

    @GET("quote")
    suspend fun getQuote(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("type") type: QuoteType,
    ): QuoteResponse

    @POST("create")
    suspend fun createOrder(
        @Body body: CreateBuyOrderRequest
    ): CreateBuyOrderResponse

    @POST("ach/create")
    suspend fun createAchOrder(
        @Body body: CreateBuyAchOrderRequest
    ): CreateBuyOrderResponse

    @GET("plaid-link-token")
    suspend fun getPlaidLinkToken(): PlaidLinkResponse

    @POST("plaid-public-token")
    suspend fun postPlaidToken(
        @Body request: PostPlaidTokenRequest
    ): Response<Unit>

    @POST("plaid-log-event")
    suspend fun postPlaidEvent(
        @Body request: PostPlaidEventRequest
    ): Response<Unit>

    @POST("plaid-log-link-error")
    suspend fun postPlaidError(
        @Body request: PostPlaidErrorRequest
    ): Response<Unit>
}