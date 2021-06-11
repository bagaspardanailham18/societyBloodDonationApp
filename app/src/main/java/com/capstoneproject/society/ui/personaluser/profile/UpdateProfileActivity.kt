package com.capstoneproject.society.ui.personaluser.profile

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ActivityUpdateProfileBinding
import com.capstoneproject.society.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateProfileActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityUpdateProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var storage: FirebaseStorage

    private lateinit var auth: FirebaseAuth

    companion object {
        const val KEY_NAME = "key_name"
        const val KEY_EMAIL = "key_emai"
        const val KEY_PHONE = "key_phone"
        const val KEY_IMG = "key_img"
    }

    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.custom_toolbar_2)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.custom_toolbar_2_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitle.setText(getString(R.string.title_update_profile))

        auth = FirebaseAuth.getInstance()

        val name = intent.getStringExtra(KEY_NAME)
        val email = intent.getStringExtra(KEY_EMAIL)
        val phone = intent.getStringExtra(KEY_PHONE)
        val imgUrl = intent.getStringExtra(KEY_IMG)

        binding.edtProfileName.setText(name)
        binding.edtProfileEmail.setText(email)
        binding.edtProfilePhone.setText(phone)

        Glide.with(this)
            .load(imgUrl)
            .apply(RequestOptions().override(200,200))
            .centerCrop()
            .into(binding.imgProfile)

        binding.btnChangeImgProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding.btnUpdate.setOnClickListener(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("UpdateProfileActivity", "img was selected")
            selectedPhotoUri = data.data

            showLoading(true)

            updateImageToFirebaseStorage()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnUpdate -> {
                val name = binding.edtProfileName.text.toString()
                val email = binding.edtProfileEmail.text.toString()
                val phone = binding.edtProfilePhone.text.toString()

                if (name.isEmpty()) {
                    binding.edtProfileName.error = "Nama harus diisi"
                    binding.edtProfileName.requestFocus()
                    return
                }

                if (email.isEmpty()) {
                    binding.edtProfileEmail.error = "Email harus diisi"
                    binding.edtProfileEmail.requestFocus()
                    return
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.edtProfileEmail.error = "Email tidak valid"
                    binding.edtProfileEmail.requestFocus()
                    return
                }

                if (phone.isEmpty()) {
                    binding.edtProfilePhone.error = "Nomor telepon harus diisi"
                    binding.edtProfilePhone.requestFocus()
                    return
                }

                showLoading(true)
                performingUpdate(name, email, phone)
            }
        }
    }

    private fun updateImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val email = intent.getStringExtra(KEY_EMAIL)
        val filename = email.toString()

        val desertRef = FirebaseStorage.getInstance().getReference("/images/$filename")

        desertRef.delete().addOnSuccessListener {
            Log.d("UpdateProfileActivity", "Delete old profile image:success")
        }.addOnFailureListener {
            Log.w(ContentValues.TAG, "Delete old profile image:failure", it)
        }

        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("UpdateProfileActivity", "Successfully updated image profile: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("UpdateProfileActivity", "File Location: $it")
                    updateUserProfileUrlToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun updateUserProfileUrlToFirebaseDatabase(urlImg: String) {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("personal_user")

        val userref = currentUser?.uid?.let { ProfileFragment.databaseReference?.child(it) }

        uid?.let {
            ref.child(it).child("profileimageurl").setValue(urlImg)
        }

        val requestRef = FirebaseDatabase.getInstance().getReference("request_blood")
        requestRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(uid.toString()).exists()) {
                        lifecycleScope.launch(Dispatchers.Default) {
                            withContext(Dispatchers.Main) {
                                uid?.let {
                                    requestRef.child(it).child("profileimageurl").setValue(urlImg).addOnSuccessListener {
                                        Log.d("UpdateProfileActivity", "Finally we updated the user to firebase database")

                                        showLoading(false)

                                        Toast.makeText(this@UpdateProfileActivity, "Photo profil berhasil diubah", Toast.LENGTH_LONG).show()
                                        startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))
                                    }
                                }
                            }
                        }
                        finish()
                    } else {
                        Log.d("UpdateProfileActivity", "Finally we updated the user to firebase database")

                        showLoading(false)

                        Toast.makeText(this@UpdateProfileActivity, "Photo profil berhasil diubah", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Log.d("UpdateProfileActivity", "Finally we updated the user to firebase database")

                    showLoading(false)

                    Toast.makeText(this@UpdateProfileActivity, "Photo profil berhasil diubah", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun performingUpdate(name: String, email: String, phone: String) {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("personal_user")

        lifecycleScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                uid?.let {
                    ref.child(it).child("name").setValue(name)
                    ref.child(it).child("email").setValue(email)
                    ref.child(it).child("phonenumber").setValue(phone)
                }
            }
        }

        //  Update User Data On Request_blood
        val refRequest = FirebaseDatabase.getInstance().getReference("request_blood")
        refRequest.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(uid.toString()).exists()) {
                        lifecycleScope.launch(Dispatchers.Default) {
                            withContext(Dispatchers.Main) {
                                uid?.let {
                                    refRequest.child(it).child("name").setValue(name)
                                    refRequest.child(it).child("email").setValue(email)
                                    refRequest.child(it).child("phonenumber").setValue(phone)
                                        .addOnSuccessListener {
                                            Log.d("UpdateProfileActivity", "Finally we updated the user to firebase database")
                                            showLoading(false)
                                            Toast.makeText(this@UpdateProfileActivity, "Data profil berhasil diubah", Toast.LENGTH_LONG).show()
                                            startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))
                                        }
                                }
                            }
                        }
                        finish()
                    } else {
                        Log.d("UpdateProfileActivity", "Finally we updated the user to firebase database")
                        showLoading(false)

                        Toast.makeText(this@UpdateProfileActivity, "Data profil berhasil diubah", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))

                        finish()
                    }
                } else {
                    Log.d("UpdateProfileActivity", "Finally we updated the user to firebase database")
                    showLoading(false)

                    Toast.makeText(this@UpdateProfileActivity, "Data profil berhasil diubah", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@UpdateProfileActivity, MainActivity::class.java))

                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}