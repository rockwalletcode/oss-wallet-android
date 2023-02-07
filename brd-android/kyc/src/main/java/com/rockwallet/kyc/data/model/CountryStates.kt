package com.rockwallet.kyc.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CountryStates(
    val data: Array<CountryState>
): Parcelable