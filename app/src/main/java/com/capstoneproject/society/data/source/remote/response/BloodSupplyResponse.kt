package com.capstoneproject.society.data.source.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BloodSupplyResponse(

	@field:SerializedName("Response")
	val response: List<BloodSupplyResponseItem>
)

data class BloodSupplyResponseItem(

	@field:SerializedName("code")
	@Expose
	val code: String? = null,

	@field:SerializedName("organizationName")
	@Expose
	val organizationName: String? = null,

	@field:SerializedName("contact")
	@Expose
	val contact: String? = null,

	@field:SerializedName("bloodType")
	@Expose
	val bloodType: String? = null,

	@field:SerializedName("supply")
	@Expose
	val supply: Int? = null,

	@field:SerializedName("subDistrict")
	@Expose
	val subDistrict: String? = null
)
