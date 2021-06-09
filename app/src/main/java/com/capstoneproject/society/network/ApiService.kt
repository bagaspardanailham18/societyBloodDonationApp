package com.capstoneproject.society.network

import retrofit2.Call
import com.capstoneproject.society.data.source.remote.response.BloodSupplyResponse
import com.capstoneproject.society.data.source.remote.response.BloodSupplyResponseItem
import com.capstoneproject.society.data.source.remote.response.SupplyRequest
import retrofit2.http.*

interface ApiService {
    @GET("organizations/blood-type/{bloodType}?excludeSupply=0")
    fun getSupplyByBloodtype(
        @Path("bloodType") bloodType: String
    ) : Call<BloodSupplyResponse>

    @PUT("organizations/{code}/{bloodType}")
    fun updateSupply(
        @Path("code") code: String,
        @Path("bloodType") bloodType: String,
        @Body req: SupplyRequest
    ) : Call<BloodSupplyResponse>


}