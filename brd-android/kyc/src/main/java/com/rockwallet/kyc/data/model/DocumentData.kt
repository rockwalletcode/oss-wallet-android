package com.rockwallet.kyc.data.model

import android.net.Uri
import android.os.Parcelable
import com.rockwallet.kyc.data.enums.DocumentSide
import kotlinx.parcelize.Parcelize

@Parcelize
data class DocumentData(
    val documentSide: DocumentSide,
    val imageUri: Uri
) : Parcelable