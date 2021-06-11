package com.capstoneproject.society.ui.auth.register.personal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.capstoneproject.society.databinding.FragmentPersonalRegisterBinding
import com.capstoneproject.society.model.PersonalUser
import com.capstoneproject.society.model.SubdistrictItems
import com.capstoneproject.society.ui.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PersonalRegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentPersonalRegisterBinding? = null

    private val binding get() = _binding!!

    private val namaSubdistrict: MutableList<String> = ArrayList()

    private val listSubdistrict = MutableLiveData<ArrayList<SubdistrictItems>>()

    @SuppressLint("WeekBasedYear")
    private var formatDate = SimpleDateFormat("dd-MMM-YYYY", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPersonalRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onResume() {
        super.onResume()
        val bloodTypes = resources.getStringArray(R.array.blood_type_register)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_bloodtype_item, bloodTypes)
        binding.edtRegisterBloodtype.setAdapter(arrayAdapter)

        val gender = resources.getStringArray(R.array.gender)
        val arrayGenderAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_bloodtype_item, gender)
        binding.edtRegisterGender.setAdapter(arrayGenderAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edtRegisterCity.setText("Bandung")
        binding.edtRegisterCity.isEnabled = false

        binding.toLogin.setOnClickListener(this)
        binding.btnPersonalRegister.setOnClickListener(this)

        loadDataSubdistrict()

        binding.edtRegisterDate.setOnClickListener {
            setDatePicker()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toLogin -> {
                activity?.finish()
            }
            R.id.btn_personal_register -> {
                val name = binding.edtRegisterNamePersonal.text.toString()
                val bloodtype = binding.edtRegisterBloodtype.text.toString()
                val email = binding.edtRegisterEmailPersonal.text.toString().trim()
                val phone = binding.edtRegisterPhone.text.toString().trim()
                val date = binding.edtRegisterDate.text.toString()
                val gender = binding.edtRegisterGender.text.toString()
                val city = binding.edtRegisterCity.text.toString()
                val subdistrict = binding.edtRegisterSubdistrict.text.toString()
                val password = binding.edtRegisterPassword.text.toString().trim()
                val passwordConfirm = binding.edtRegisterConfirmPassword.text.toString().trim()

                if (name.isEmpty()) {
                    binding.edtRegisterNamePersonal.error = "Nama harus diisi"
                    binding.edtRegisterNamePersonal.requestFocus()
                    return
                }
                if (bloodtype.isEmpty()) {
                    binding.edtRegisterBloodtype.error = "Tipe darah harus dipilih"
                    binding.edtRegisterBloodtype.requestFocus()
                    return
                }
                if (email.isEmpty()) {
                    binding.edtRegisterEmailPersonal.error = "Email harus diisi"
                    binding.edtRegisterEmailPersonal.requestFocus()
                    return
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.edtRegisterEmailPersonal.error = "Email tidak valid"
                    binding.edtRegisterEmailPersonal.requestFocus()
                    return
                }
                if (phone.isEmpty()) {
                    binding.edtRegisterPhone.error = "No telepon harus diisi"
                    binding.edtRegisterPhone.requestFocus()
                    return
                }

                if (date.isEmpty()) {
                    binding.edtRegisterDate.error = "Tanggal lahir harus diisi"
                    binding.edtRegisterDate.requestFocus()
                    return
                }

                if (gender.isEmpty()) {
                    binding.edtRegisterGender.error = "Jenis kelamin harus diisi"
                    binding.edtRegisterGender.requestFocus()
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
        val email = binding.edtRegisterEmailPersonal.text.toString()
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
        val filename = binding.edtRegisterEmailPersonal.text.toString()
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

        val name = binding.edtRegisterNamePersonal.text.toString().trim()
        val email = binding.edtRegisterEmailPersonal.text.toString().trim()
        val bloodtype = binding.edtRegisterBloodtype.text.toString()
        val phonenumber = binding.edtRegisterPhone.text.toString().trim()
        val date = binding.edtRegisterDate.text.toString()
        val gender = binding.edtRegisterGender.text.toString()
        val city = binding.edtRegisterCity.text.toString()
        val subdistrict = binding.edtRegisterSubdistrict.text.toString()
        val usertype = "Personal"
        val donorsucces = 0

        val user = PersonalUser(uid, name, email, phonenumber, city, subdistrict, bloodtype, date, gender, usertype, profileImageUrl, donorsucces)
        ref.child(uid).setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", name)
                Log.d("RegisterActivity", email)
                Log.d("RegisterActivity", bloodtype)
                Log.d("RegisterActivity", phonenumber)
                Log.d("RegisterActivity", city)
                Log.d("RegisterActivity", subdistrict)
                Log.d("RegisterActivity", "Finally we saved the user to firebase database")
            }
    }

    private fun loadDataSubdistrict() {
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

                                namaSubdistrict.add(jsonObject.getString("nama"))

                                listItems.add(subdistrictItems)
                                val arrayAdapter = ArrayAdapter(
                                    requireContext(), android.R.layout.simple_list_item_1, namaSubdistrict)
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.edtRegisterSubdistrict.setAdapter(arrayAdapter)
                            }
                        }
                        listSubdistrict.postValue(listItems)
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

    private fun setDatePicker() {
        val getDate = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            val selectDate = Calendar.getInstance()
            selectDate.set(Calendar.YEAR, year)
            selectDate.set(Calendar.MONTH, month)
            selectDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = formatDate.format(selectDate.time)
            binding.edtRegisterDate.setText(date)

        }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }


}