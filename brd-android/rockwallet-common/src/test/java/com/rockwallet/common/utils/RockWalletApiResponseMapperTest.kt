package com.rockwallet.common.utils

import com.rockwallet.common.data.RockWalletApiResponse
import com.rockwallet.common.data.RockWalletApiResponseError
import com.rockwallet.common.data.Resource
import com.rockwallet.common.data.Status
import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RockWalletApiResponseMapperTest {

    @Mock lateinit var moshi: Moshi

    private lateinit var mapper: RockWalletApiResponseMapper

    @Before
    fun setUp() {
        mapper = RockWalletApiResponseMapper(moshi)
    }

    @Test
    fun mapRokWalletApiResponseSuccess_responseOk_returnSuccess() {
        val response: RockWalletApiResponse<String?> = RockWalletApiResponse(
            result = "ok",
            error = null,
            data = "test"
        )

        val actual: Resource<String?> = mapper.mapApiResponseSuccess(response)

        assertEquals(response.data, actual.data)
        assertEquals(Status.SUCCESS, actual.status)
    }

    @Test
    fun mapRokWalletApiResponseSuccess_responseErrorWithErrorObject_returnError() {
        val response: RockWalletApiResponse<String?> = RockWalletApiResponse(
            result = "error",
            error = RockWalletApiResponseError(
                code = "2100",
                message = "Test error code 2100"
            ),
            data = null
        )

        val actual: Resource<String?> = mapper.mapApiResponseSuccess(response)

        assertEquals("2100", actual.message)
        assertEquals(Status.ERROR, actual.status)
    }

    @Test
    fun mapRokWalletApiResponseSuccess_responseErrorWithoutErrorObject_returnUnknownError() {
        val response: RockWalletApiResponse<String?> = RockWalletApiResponse(
            result = "error",
            error = null,
            data = null
        )

        val actual: Resource<String?> = mapper.mapApiResponseSuccess(response)

        assertEquals("Unknown error - invalid state", actual.message)
        assertEquals(Status.ERROR, actual.status)
    }
}