package com.capstoneproject.society.ui.organization.ui.requestlist

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.capstoneproject.society.databinding.ActivityDetailRequestOrgBinding
import com.capstoneproject.society.model.RequestItems
import com.capstoneproject.society.ui.personaluser.home.requestlist.DetailRequestActivity

class DetailRequestOrgActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailRequestOrgBinding

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRequestOrgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataDetail = intent.getParcelableExtra<RequestItems>(DetailRequestActivity.EXTRA_DETAIL) as RequestItems
        val name = dataDetail.name
        val img = dataDetail.profileimageurl
        val location = "${dataDetail.subdistrict}, ${dataDetail.city}"
        val blood = dataDetail.bloodtyperequest
        val phone = dataDetail.phonenumber

        Glide.with(this)
            .load(img)
            .centerCrop()
            .into(binding.tvUserImg)

        binding.tvName.text = name
        binding.tvUserLoc.text = location
        binding.tvBloodtype.text = blood
        binding.tvPhone.text = phone

        binding.btnCall.setOnClickListener {
            callPerson(phone)
        }
    }

    private fun callPerson(phone: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + phone)
        startActivity(dialIntent)
    }
}