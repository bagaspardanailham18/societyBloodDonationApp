package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubdistrictItems(
    var id: Int? = null,
    var id_city: String? = null,
    var name: String? = null
) : Parcelable
