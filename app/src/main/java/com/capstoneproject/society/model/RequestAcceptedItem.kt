package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestAcceptedItem(
        var id: String = "",
        var idDonor: String = "",
        var idPatient: String = "",
        var donorName: String = "",
        var donorImgUrl: String = "",
        var date: String = "",
        var note: String = ""
) : Parcelable