package com.capstoneproject.society.data.source.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SupplyRequest {
    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("organizationName")
    @Expose
    var organizationName: String? = null

    @SerializedName("subDistrict")
    @Expose
    var subDistrict: String? = null

    @SerializedName("contact")
    @Expose
    var contact: String? = null

    @SerializedName("bloodType")
    @Expose
    var bloodType: String? = null

    @SerializedName("supply")
    @Expose
    var supply: Int? = null
}