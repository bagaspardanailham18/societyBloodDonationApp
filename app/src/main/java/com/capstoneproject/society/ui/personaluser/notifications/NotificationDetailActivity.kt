package com.capstoneproject.society.ui.personaluser.notifications

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ActivityNotificationDetailBinding
import com.capstoneproject.society.model.RequestAcceptedItem
import com.capstoneproject.society.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationDetailBinding

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.custom_toolbar_2)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.custom_toolbar_2_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitle.setText(getString(R.string.title_detail_notif))

        val data = intent.getParcelableExtra(EXTRA_DATA) as RequestAcceptedItem?

        val donorRef = FirebaseDatabase.getInstance().getReference("personal_user").child(data?.idDonor.toString())

        getData(data, donorRef)
    }

    private fun getData(data: RequestAcceptedItem?, donorRef: DatabaseReference) {
        donorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val idRequest = data?.id.toString()
                val name = data?.donorName.toString()
                val imgUrl = data?.donorImgUrl.toString()
                val usertype = snapshot.child("usertype").value.toString()
                val contact = snapshot.child("phonenumber").value.toString()
                val subdistrict = snapshot.child("subdistrict").value
                val city = snapshot.child("city").value
                val location = "$subdistrict, $city"
                val note = data?.note.toString()
                val dateofbirth = snapshot.child("dateofbirth").value.toString()
                val gender = snapshot.child("gender").value.toString()
                val donorSuccess = snapshot.child("donorSuccess").value.toString()

                val idDonor = snapshot.child("uid").value.toString()

                populateData(name, imgUrl, usertype, contact, location, idRequest, note, dateofbirth, gender, donorSuccess, idDonor)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun populateData(
            name: String,
            imgUrl: String,
            usertype: String,
            contact: String,
            location: String,
            idRequest: String,
            note: String,
            dateofbirth: String,
            gender: String,
            donorSuccess: String,
            idDonor: String
    ) {
        Glide.with(this)
            .load(imgUrl)
            .into(binding.tvUserImg)

        binding.tvName.text = name
        binding.tvUserType.text = usertype
        binding.tvUserLoc.text = location
        binding.tvNote.text = note
        binding.tvUserDateOfbirt.text = dateofbirth
        binding.tvUserGender.text = gender
        binding.tvDonorSuccess.text = donorSuccess
        binding.tvContact.text = contact

        binding.btnCall.setOnClickListener{
            btnCallClicked(contact)
        }

        binding.btnReject.setOnClickListener {
            btnRejectClicked(idRequest)
        }

        binding.btnFinish.setOnClickListener {
            btnFinishClicked(idRequest, idDonor)
        }
    }

    private fun btnCallClicked(contact: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + contact)
        startActivity(dialIntent)
    }

    private fun btnRejectClicked(idRequest: String) {
        val dbRecAccRef = FirebaseDatabase.getInstance().getReference("request_accepted").child(idRequest)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Apakah anda yakin")

        alertDialogBuilder.setPositiveButton("Ya") { p0, p1 ->
            dbRecAccRef.removeValue()
            Toast.makeText(this, "Donor ditolak", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }

        alertDialogBuilder.show()
    }

    private fun btnFinishClicked(idRequest: String, idDonor: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val dbReqRef = FirebaseDatabase.getInstance().getReference("request_blood").child(uid.toString())
        val dbRecAccRef = FirebaseDatabase.getInstance().getReference("request_accepted").child(idRequest)
        val dbDonorRef = FirebaseDatabase.getInstance().getReference("personal_user").child(idDonor)

        dbDonorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentDonorSuccess = snapshot.child("donorSuccess").value.toString().toInt()
                val donorSuccessAdd = "${currentDonorSuccess.plus(1)}"

                val alertDialogBuilder = AlertDialog.Builder(this@NotificationDetailActivity)
                alertDialogBuilder.setMessage("Apakah anda yakin")

                alertDialogBuilder.setPositiveButton("Ya") { p0, p1 ->
                    dbReqRef.removeValue()
                    dbRecAccRef.removeValue()

                    lifecycleScope.launch(Dispatchers.Default) {
                        withContext(Dispatchers.Main) {
                            dbDonorRef.child("donorSuccess").setValue(donorSuccessAdd)
                        }
                    }

                    Toast.makeText(this@NotificationDetailActivity, "Donor selesai", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@NotificationDetailActivity, MainActivity::class.java))
                    finish()
                }

                alertDialogBuilder.show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}