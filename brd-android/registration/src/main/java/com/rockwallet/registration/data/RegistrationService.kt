package com.rockwallet.registration.data

import com.rockwallet.common.data.RockWalletApiResponse
import com.rockwallet.common.data.model.Profile
import com.rockwallet.registration.data.requests.AssociateConfirmRequest
import com.rockwallet.registration.data.requests.AssociateEmailRequest
import com.rockwallet.registration.data.requests.AssociateNewDeviceRequest
import com.rockwallet.registration.data.responses.AssociateEmailResponse
import com.rockwallet.registration.data.responses.AssociateNewDeviceResponse
import retrofit2.Response
import retrofit2.http.*

interface RegistrationService {

    @POST("associate")
    suspend fun associateEmail(
        @HeaderMap headers: Map<String, String?>,
        @Body request: AssociateEmailRequest
    ): RockWalletApiResponse<AssociateEmailResponse?>

    @POST("new-device")
    suspend fun associateNewDevice(
        @HeaderMap headers: Map<String, String?>,
        @Body request: AssociateNewDeviceRequest
    ): AssociateNewDeviceResponse

    @POST("associate/confirm")
    suspend fun associateAccountConfirm(
        @Body request: AssociateConfirmRequest
    ): Response<Unit>

    @POST("associate/resend")
    suspend fun resendAssociateAccountChallenge(): Response<Unit>

    @GET("profile")
    suspend fun getProfile(): Profile

    @DELETE("profile")
    suspend fun deleteProfile()
}