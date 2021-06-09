package com.capstoneproject.society.ui.organization.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.FragmentOrgProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrgProfileFragment : Fragment() {

    private var _binding: FragmentOrgProfileBinding? = null
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
        _binding = FragmentOrgProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("personal_user")

        loadProfile()
    }

    private fun loadProfile() {
        val user = auth.currentUser
        val userref = user?.uid?.let { databaseReference?.child(it) }

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
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}