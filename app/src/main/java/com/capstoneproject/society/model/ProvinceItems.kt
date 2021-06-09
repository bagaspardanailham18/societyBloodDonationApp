package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProvinceItems(
    var id: Int? = null,
    var name: String? = null,
) : Parcelable
