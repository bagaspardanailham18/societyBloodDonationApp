package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CityItems(
    var id: Int? = null,
    var id_province: String? = null,
    var name: String? = null
) : Parcelable
