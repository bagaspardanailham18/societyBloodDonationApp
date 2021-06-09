package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrganizationSupplyItem(
        var code: String? = null,
        var organizationName: String? = null,
        var subDistrict: String? = null,
        var contact: String? = null,
        var bloodType: String? = null,
        var supply: Int? = null,
) : Parcelable
