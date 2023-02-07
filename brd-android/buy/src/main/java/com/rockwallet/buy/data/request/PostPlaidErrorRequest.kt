package com.rockwallet.buy.data.request

import com.plaid.link.result.LinkExit
import com.squareup.moshi.JsonClass
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@JsonClass(generateAdapter = true)
data class PostPlaidErrorRequest(
    @com.squareup.moshi.Json(name = "plaid_link_error")
    val errorJson: String
) {
    companion object {
        fun from(linkExit: LinkExit): PostPlaidErrorRequest {
            val map = mutableMapOf<String, String>()

            linkExit.error?.let {
                map["error_code"] = it.errorCode.json
                map["error_message"] = it.errorMessage
            }

            linkExit.metadata.institution?.let {
                map["institution_id"] = it.id
                map["institution_name"] = it.name
            }

            linkExit.metadata.requestId?.let { map["request_id"] = it }
            linkExit.metadata.linkSessionId?.let { map["link_session_id"] = it }
            linkExit.metadata.status?.jsonValue?.let { map["status"] = it }

            return PostPlaidErrorRequest(
                Json.encodeToString(map)
            )
        }
    }
}
