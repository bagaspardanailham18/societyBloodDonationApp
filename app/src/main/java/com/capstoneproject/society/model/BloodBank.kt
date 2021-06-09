package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BloodBank(
    val uid: String? = "",
    val bloodtype: String = "",
    val quantity: String = ""
) : Parcelable
