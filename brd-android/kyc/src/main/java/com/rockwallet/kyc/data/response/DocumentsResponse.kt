package com.rockwallet.kyc.data.response

import com.rockwallet.kyc.data.enums.DocumentType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DocumentsResponse(
    @Json(name = "supportedDocuments")
    val documents: List<DocumentType>
)