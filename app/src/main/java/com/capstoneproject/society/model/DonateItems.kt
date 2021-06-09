package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DonateItems(
    var id: String? = "",
    var uid: String? = "",
    var patientId: String? = "",
    var hospital: String? = "",
    var dateDonate: String? = ""
) : Parcelable
