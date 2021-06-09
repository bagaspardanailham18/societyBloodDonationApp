package com.capstoneproject.society.ui.auth.register.organization

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.FragmentOrganizationRegisterBinding
import com.capstoneproject.society.model.OrganizationUser
import com.capstoneproject.society.model.SubdistrictItems
import com.capstoneproject.society.ui.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class OrganizationRegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentOrganizationRegisterBinding? = null

    private val binding get() = _binding!!

    private val listSubdistricts = MutableLiveData<ArrayList<SubdistrictItems>>()
    private val namaSubdisctrict: MutableList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrganizationRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edtRegisterCity.setText("Bandung")
        binding.edtRegisterCity.isEnabled = false

        loadDataSubdistricts()

        binding.toLogin.setOnClickListener(this)
        binding.btnOrgRegister.setOnClickListener(this)
    }

    private fun loadDataSubdistricts() {
        val listItems = ArrayList<SubdistrictItems>()

        AndroidNetworking.get("https://dev.farizdotid.com/api/daerahindonesia/kecamatan?id_kota=3204")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    try {
                        val jsonArray = response?.getJSONArray("kecamatan")
                        if (jsonArray != null) {
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val subdistrictItems = SubdistrictItems()
                                subdistrictItems.id = jsonObject.getInt("id")
                                subdistrictItems.id_city = jsonObject.getString("id_kota")
                                subdistrictItems.name = jsonObject.getString("nama")

                                namaSubdisctrict.add(jsonObject.getString("nama"))
//                                listProvince.add(jsonObject.getString("id"))

                                listItems.add(subdistrictItems)
                                val arrayAdapter = ArrayAdapter(
                                    requireContext(), android.R.layout.simple_list_item_1, namaSubdisctrict)
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.edtRegisterSubdistrict.setAdapter(arrayAdapter)
                            }
                        }
                        listSubdistricts.postValue(listItems)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Gagal menampilkan data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(requireContext(), "Tidak ada jaringan internet", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toLogin -> {
                activity?.finish()
            }
            R.id.btn_org_register -> {
                val name = binding.edtRegisterNameOrg.text.toString()
                val email = binding.edtRegisterEmailOrg.text.toString().trim()
                val phone = binding.edtRegisterPhone.text.toString().trim()
                val city = binding.edtRegisterCity.text.toString()
                val subdistrict = binding.edtRegisterSubdistrict.text.toString()
                val password = binding.edtRegisterPassword.text.toString().trim()
                val passwordConfirm = binding.edtRegisterConfirmPassword.text.toString().trim()

                if (name.isEmpty()) {
                    binding.edtRegisterNameOrg.error = "Nama organisasi harus diisi"
                    binding.edtRegisterNameOrg.requestFocus()
                    return
                }
                if (email.isEmpty()) {
                    binding.edtRegisterEmailOrg.error = "Email harus diisi"
                    binding.edtRegisterEmailOrg.requestFocus()
                    return
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.edtRegisterEmailOrg.error = "Email tidak valid"
                    binding.edtRegisterEmailOrg.requestFocus()
                    return
                }
                if (phone.isEmpty()) {
                    binding.edtRegisterPhone.error = "No telepon harus diisi"
                    binding.edtRegisterPhone.requestFocus()
                    return
                }
                if (city.isEmpty()) {
                    binding.edtRegisterCity.error = "Kota harus dipilih"
                    binding.edtRegisterCity.requestFocus()
                    return
                }
                if (subdistrict.isEmpty()) {
                    binding.edtRegisterSubdistrict.error = "Kecamatan harus dipilih"
                    binding.edtRegisterSubdistrict.requestFocus()
                    return
                }
                if (password.isEmpty()) {
                    binding.edtRegisterPassword.error = "Password harus diisi"
                    binding.edtRegisterPassword.requestFocus()
                    return
                }
                if (passwordConfirm.isEmpty()) {
                    binding.edtRegisterConfirmPassword.error = "Konfirmasi password harus diisi"
                    binding.edtRegisterConfirmPassword.requestFocus()
                    return
                }
                if (passwordConfirm != password) {
                    binding.edtRegisterConfirmPassword.error = "Konfirmasi password tidak sesuai"
                    binding.edtRegisterConfirmPassword.requestFocus()
                    return
                }

                performingRegister()
            }
        }
    }

    private fun performingRegister() {
        val email = binding.edtRegisterEmailOrg.text.toString()
        val password = binding.edtRegisterPassword.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("RegisterActivity", "createUserWithEmail:success")
                    Log.d("RegisterActivity", "Email: $email | Pass: $password")

                    uploadImageToFirebaseStorage()
                    Toast.makeText(requireContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show()

                    val user = Firebase.auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(requireContext(), LoginActivity::class.java))
                                activity?.finish()
                            }
                        }
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", it.exception)
                    Log.d(ContentValues.TAG, "Email: $email | Pass: $password")
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        val filename = UUID.randomUUID().toString()
        val uriImage = Uri.parse("android.resource://com.capstoneproject.society/" + R.drawable.user)
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(uriImage)
            .addOnSuccessListener { it ->
                Log.d("RegisterActivity", "Upload image berhasil: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("personal_user")

        val name = binding.edtRegisterNameOrg.text.toString().trim()
        val email = binding.edtRegisterEmailOrg.text.toString().trim()
        val phonenumber = binding.edtRegisterPhone.text.toString().trim()
        val city = binding.edtRegisterCity.text.toString()
        val subdistrict = binding.edtRegisterSubdistrict.text.toString()
        val usertype = "Organization"

        val user = OrganizationUser(uid, name, email, phonenumber, city, subdistrict, usertype, profileImageUrl)
        ref.child(uid).setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", name)
                Log.d("RegisterActivity", email)
                Log.d("RegisterActivity", phonenumber)
                Log.d("RegisterActivity", city)
                Log.d("RegisterActivity", subdistrict)
                Log.d("RegisterActivity", "Finally we saved the user to firebase database")
            }
    }
}