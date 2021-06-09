package com.capstoneproject.society.ui.personaluser.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    companion object {
        var databaseReference: DatabaseReference? = null
        var database: FirebaseDatabase? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("personal_user")

        val user = auth.currentUser
        val userref = user?.uid?.let { databaseReference?.child(it) }

        loadProfile(user, userref)

        binding.btnChangeData.setOnClickListener {
            toUpdateProfileActivity(user, userref)
        }
    }

    @SuppressLint("CheckResult")
    private fun loadProfile(user: FirebaseUser?, userref: DatabaseReference?) {

        binding.tvEmail.setText(user?.email)

        userref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Glide.with(requireContext())
                    .load(snapshot.child("profileimageurl").value.toString())
                    .apply(RequestOptions().override(200,200))
                    .centerCrop()
                    .into(binding.imgProfile)

                binding.tvName.setText(snapshot.child("name").value.toString())
                binding.tvPhone.setText(snapshot.child("phonenumber").value.toString())
                binding.tvBloodtype.setText(snapshot.child("bloodtype").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun toUpdateProfileActivity(user: FirebaseUser?, userref: DatabaseReference?) {
        userref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imgUrl = snapshot.child("profileimageurl").value.toString()
                val name = binding.tvName.text.toString()
                val email = binding.tvEmail.text.toString()
                val phone = binding.tvPhone.text.toString()

                val intent = Intent(requireContext(), UpdateProfileActivity::class.java).apply {
                    putExtra(UpdateProfileActivity.KEY_NAME, name)
                    putExtra(UpdateProfileActivity.KEY_EMAIL, email)
                    putExtra(UpdateProfileActivity.KEY_PHONE, phone)
                    putExtra(UpdateProfileActivity.KEY_IMG, imgUrl)
                }
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}