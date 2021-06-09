package com.capstoneproject.society.ui.personaluser.home.finddonor

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.capstoneproject.society.data.source.remote.response.BloodSupplyResponse
import com.capstoneproject.society.data.source.remote.response.BloodSupplyResponseItem
import com.capstoneproject.society.data.source.remote.response.SupplyRequest
import com.capstoneproject.society.databinding.ActivityDetailSupplyBinding
import com.capstoneproject.society.databinding.ActivityFindDonorBinding
import com.capstoneproject.society.model.OrganizationSupplyItem
import com.capstoneproject.society.network.ApiConfig
import com.capstoneproject.society.network.ApiService
import com.capstoneproject.society.ui.MainActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class DetailSupplyActivity : AppCompatActivity() {

    private var _binding: ActivityDetailSupplyBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailSupplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra(EXTRA_DETAIL) as OrganizationSupplyItem?

        populateItem(data)
    }

    private fun populateItem(data: OrganizationSupplyItem?) {
        binding.tvName.text = data?.organizationName
        binding.tvUserLoc.text = data?.subDistrict
        binding.tvBloodtype.text = data?.bloodType
        binding.tvSupply.text = data?.supply.toString()
        binding.tvPhone.text = data?.contact

        binding.btnCall.setOnClickListener {
            callPerson(data?.contact)
        }

        binding.btnRequest.setOnClickListener {
            if (data != null) {
                edtSupplyOrganization(
                        data.code.toString(),
                        data.bloodType.toString(),
                        data.organizationName.toString(),
                        data.subDistrict.toString(),
                        data.contact.toString(),
                        data.supply!!)
            }
        }
    }

    private fun edtSupplyOrganization(code: String, bloodtype: String, organizationName: String, subdistrict: String, contact: String, supply: Int) {

        val supplyReq = SupplyRequest()
        supplyReq.code = code
        supplyReq.organizationName = organizationName
        supplyReq.subDistrict = subdistrict
        supplyReq.contact = contact
        supplyReq.bloodType = bloodtype
        supplyReq.supply = supply.minus(1)

        val config = ApiConfig().getRetroClientInstance().create(ApiService::class.java)
        config.updateSupply(code, bloodtype, supplyReq).enqueue(object : retrofit2.Callback<BloodSupplyResponse> {
            override fun onResponse(call: Call<BloodSupplyResponse>, response: Response<BloodSupplyResponse>) {
                Toast.makeText(this@DetailSupplyActivity, "Berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@DetailSupplyActivity, MainActivity::class.java))
                finish()
                Log.e("Success", "Request berhasil dikirim")
            }

            override fun onFailure(call: Call<BloodSupplyResponse>, t: Throwable) {
                Log.e("Failed", t.message.toString())
            }


        })
    }

    private fun callPerson(contact: String?) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + contact)
        startActivity(dialIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}