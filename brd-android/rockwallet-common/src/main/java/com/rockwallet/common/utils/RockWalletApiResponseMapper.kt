package com.rockwallet.common.utils

import android.content.Context
import android.util.Log
import com.rockwallet.common.R
import com.rockwallet.common.data.RockWalletApiResponse
import com.rockwallet.common.data.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class RockWalletApiResponseMapper(
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
) {

    fun <T> mapApiResponseSuccess(response: RockWalletApiResponse<T?>): Resource<T?> {
        return when {
            response.result == "ok" ->
                Resource.success(response.data)
            response.result == "error" && response.error != null ->
                Resource.error(message = response.error.code)
            else ->
                Resource.error(message = "Unknown error - invalid state")
        }
    }

    fun <T> mapResponseSuccess(context: Context, response: Response<Unit>): Resource<T?> {
        return if (response.isSuccessful) {
            Resource.success(data = null)
        } else {
            val message = getErrorMessage(response.errorBody())
            Resource.error(message = message ?: context.getString(R.string.ErrorMessages_default))
        }
    }

    fun <T> mapError(context: Context, exception: Exception): Resource<T?> {
        var errorMessage: String? = null

        if (exception is HttpException) {
            exception.response()?.errorBody()?.let {
                errorMessage = getErrorMessage(it)
            }
        }

        return Resource.error(
            message = errorMessage ?: context.getString(R.string.ErrorMessages_default)
        )
    }

    private fun getErrorMessage(errorBody: ResponseBody?): String? {
        return errorBody?.let {
            val responseType =
                Types.newParameterizedType(RockWalletApiResponse::class.java, Any::class.java)
            val responseAdapter = moshi.adapter<RockWalletApiResponse<Any>>(responseType)

            try {
                val errorJson = it.string()
                val response = responseAdapter.fromJson(errorJson)
                response?.error?.message
            } catch (ex: Exception) {
                Log.d("RockWalletApiResponseMapper", "Parsing exception ${ex.message ?: "unknown"}")
                null
            }
        }
    }
}