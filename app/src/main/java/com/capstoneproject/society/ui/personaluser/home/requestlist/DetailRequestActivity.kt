package com.capstoneproject.society.ui.personaluser.home.requestlist

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ActivityDetailRequestBinding
import com.capstoneproject.society.model.RequestAcceptedItem
import com.capstoneproject.society.model.RequestItems
import com.capstoneproject.society.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailRequestActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailRequestBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var dialog: Dialog

    companion object {
        const val EXTRA_DETAIL = "extra_detail"

        var databaseReference: DatabaseReference? = null
        var database: FirebaseDatabase? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("personal_user")

        val user = auth.currentUser

        val dataDetail = intent.getParcelableExtra<RequestItems>(EXTRA_DETAIL) as RequestItems
        val uidRequest = dataDetail.uid
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

        binding.btnDonate.setOnClickListener {
            showDonateDialog(user, uidRequest)
        }
    }

    @SuppressLint("ResourceType")
    private fun showDonateDialog(user: FirebaseUser?, uidRequest: String) {

        val uid = user?.uid.toString()
        val idUserReq = uidRequest

        dialog.setContentView(R.layout.donate_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val btnSubmit: Button = dialog.findViewById(R.id.btn_submit)
        val edtNote = dialog.findViewById<EditText>(R.id.edt_note)
        val note = edtNote.text.toString().trim()
        val idRequest = randomID()


        btnSubmit.setOnClickListener{
            saveRequestAccepted(uid, idUserReq, note, idRequest)
            finish()
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            dialog.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        } else {
            dialog.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        }
    }

    private fun saveRequestAccepted(uid: String, idUserReq: String, note: String, idRequest: String) {

        val userRef = FirebaseDatabase.getInstance().reference.child("personal_user").child(uid)
        userRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val donorName = snapshot.child("name").value.toString()
                val donorImgUrl = snapshot.child("profileimageurl").value.toString()

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val datetime = current.format(formatter)

                val dbRequestAcc = FirebaseDatabase.getInstance().getReference("request_accepted")
                val reqAcc = RequestAcceptedItem(idRequest, uid, idUserReq, donorName, donorImgUrl, datetime, note)

                dbRequestAcc.child(idRequest).setValue(reqAcc)
                val intent = Intent(this@DetailRequestActivity, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this@DetailRequestActivity, "Request Accepted", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun callPerson(phone: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + phone)
        startActivity(dialIntent)
    }

    private fun randomID(): String = List(16) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}