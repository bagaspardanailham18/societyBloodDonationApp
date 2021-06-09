package com.capstoneproject.society.ui.personaluser.home.finddonor

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.society.model.OrganizationSupplyItem
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import java.lang.Exception

class FindDonorViewModel: ViewModel() {
    private val listOrgSupply = MutableLiveData<ArrayList<OrganizationSupplyItem>>()

    fun setSupply(bloodtype: String, context: Context) {
        val listItems = ArrayList<OrganizationSupplyItem>()

        val url = "https://blood-donor-api-3fdpgwxc5a-et.a.run.app/organizations/blood-type/$bloodtype?excludeSupply=0"

        val client = AsyncHttpClient()

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }
                try {
                    val responseObject =JSONArray(result)

                    for (i in 0 until responseObject.length()) {
                        val supply = responseObject.getJSONObject(i)
                        val supplyItems = OrganizationSupplyItem()
                        val orgCode = supply.getString("code")
                        val bloodtype = supply.getString("bloodType")
                        supplyItems.code = orgCode
                        supplyItems.organizationName = supply.getString("organizationName")
                        supplyItems.contact = supply.getString("contact")
                        supplyItems.subDistrict = supply.getString("subDistrict")
                        supplyItems.bloodType = bloodtype
                        supplyItems.supply = supply.getInt("supply")

                        listItems.add(supplyItems)
                    }
                    listOrgSupply.postValue(listItems)
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun getSupplies() : LiveData<ArrayList<OrganizationSupplyItem>> {
        return listOrgSupply
    }
}