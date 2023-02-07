package com.rockwallet.buy.data.request

import com.plaid.link.event.LinkEvent
import com.squareup.moshi.JsonClass
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@JsonClass(generateAdapter = true)
data class PostPlaidEventRequest(
    @com.squareup.moshi.Json(name = "plaid_callback_event")
    val eventJson: String
) {
    companion object {
        fun from(event: LinkEvent): PostPlaidEventRequest {
            val map = event.metadata.toMap().toMutableMap().apply {
                put("event_name", event.eventName.json)
            }

            return PostPlaidEventRequest(
                Json.encodeToString(map)
            )
        }
    }
}
