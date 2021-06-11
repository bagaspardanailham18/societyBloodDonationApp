package com.capstoneproject.society.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonalUser(
    val uid: String? = "",
    val name: String = "",
    val email: String? = "",
    val phonenumber: String = "",
    val city: String = "",
    val subdistrict: String = "",
    val bloodtype: String = "",
    val dateofbirth: String = "",
    val gender: String = "",
    val usertype: String = "",
    val profileimageurl: String ="",
    val donorSuccess: Int
) : Parcelable
