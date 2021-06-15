package com.capstoneproject.society.ui.personaluser.home.requestlist

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.capstoneproject.society.R
import com.capstoneproject.society.RetrofitInstance
import com.capstoneproject.society.databinding.ActivityFormRequestAcceptBinding
import com.capstoneproject.society.model.NotificationData
import com.capstoneproject.society.model.PushNotification
import com.capstoneproject.society.model.RequestAcceptedItem
import com.capstoneproject.society.ui.MainActivity
import com.capstoneproject.society.ui.personaluser.notifications.NotificationsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FormRequestAcceptActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityFormRequestAcceptBinding

    private lateinit var auth: FirebaseAuth

    var topic = ""

    companion object {
        const val EXTRA_UID_REQUEST = "extra_uid_request"

        var databaseReference: DatabaseReference? = null
        var database: FirebaseDatabase? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormRequestAcceptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.custom_toolbar_2)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.custom_toolbar_2_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitle.setText(getString(R.string.title_form_request_accept))

        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("personal_user")

        auth = FirebaseAuth.getInstance()

        binding.btnHelp.setOnClickListener{
            val uid = auth.currentUser?.uid.toString()
            val idUserReq = intent.getStringExtra(EXTRA_UID_REQUEST).toString()

            val note = binding.edtNote.text.toString().trim()
            val idRequest = randomID()

            if (note.isEmpty()) {
                Toast.makeText(this, "Catatan harus diisi", Toast.LENGTH_SHORT).show()
            } else {
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
                        val reqAcc = RequestAcceptedItem(idRequest,
                            uid, idUserReq, donorName, donorImgUrl, datetime, note)

                        dbRequestAcc.child(idRequest).setValue(reqAcc)

                        topic = "/topics/$idUserReq"
                        PushNotification(NotificationData("$donorName accepted your request"), topic).also {
                            sendNotification(it)
                        }

                        startActivity(Intent(this@FormRequestAcceptActivity, MainActivity::class.java))
                        Toast.makeText(this@FormRequestAcceptActivity, "Anda menerima request", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }

    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                return@launch
            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch (e: Exception) {
            Log.e("TAG", e.toString())
        }
    }

    private fun randomID(): String = List(16) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

    override fun onClick(v: View?) {
    }
}