package com.capstoneproject.society.ui.personaluser.home.requestlist

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ActivityDetailRequestBinding
import com.capstoneproject.society.model.RequestItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailRequestActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailRequestBinding

    private lateinit var dialog: Dialog

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.custom_toolbar_2)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.custom_toolbar_2_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitle.setText(getString(R.string.title_detail_request))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dialog = Dialog(this)

        val dataDetail = intent.getParcelableExtra(EXTRA_DETAIL) as RequestItems?
        val uidRequest = dataDetail?.uid
        val name = dataDetail?.name
        val img = dataDetail?.profileimageurl
        val location = "${dataDetail?.subdistrict}, ${dataDetail?.city}"
        val blood = dataDetail?.bloodtyperequest
        val phone = dataDetail?.phonenumber

        val userReqRef = FirebaseDatabase.getInstance().getReference("personal_user").child(uidRequest.toString())

        userReqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dateofbirth = snapshot.child("dateofbirth").value.toString()
                val gender = snapshot.child("gender").value.toString()

                Glide.with(this@DetailRequestActivity)
                        .load(img)
                        .centerCrop()
                        .into(binding.tvUserImg)

                binding.tvName.text = name
                binding.tvUserLoc.text = location
                binding.tvBloodtype.text = blood
                binding.tvPhone.text = phone
                binding.tvUserDate.text = dateofbirth
                binding.tvUserGender.text = gender

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        binding.btnCall.setOnClickListener {
            callPerson(phone)
        }

        binding.btnDonate.setOnClickListener {
            val intent = Intent(this, FormRequestAcceptActivity::class.java)
            intent.putExtra(FormRequestAcceptActivity.EXTRA_UID_REQUEST, uidRequest)
            startActivity(intent)
        }
    }


    private fun callPerson(phone: String?) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + phone)
        startActivity(dialIntent)
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}