package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestItems(
    var id: String = "",
    var uid: String = "",
    var name: String = "",
    var phonenumber: String = "",
    var city: String = "",
    var subdistrict: String = "",
    var bloodtyperequest: String = "",
    var usertype: String = "",
    var profileimageurl: String =""
) : Parcelable
