package com.capstoneproject.society.ui.personaluser.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.FragmentHomeBinding
import com.capstoneproject.society.ui.personaluser.home.bloodbank.BloodBankActivity
import com.capstoneproject.society.ui.personaluser.home.finddonor.FindDonorActivity
import com.capstoneproject.society.ui.personaluser.home.organizationdonor.DonorToOrganizationActivity
import com.capstoneproject.society.ui.personaluser.home.requestlist.RequestListActivity
import com.capstoneproject.society.ui.personaluser.profile.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    companion object {
        var databaseReference: DatabaseReference? = null
        var database: FirebaseDatabase? = null
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listrequest.setOnClickListener(this)
        binding.caridonor.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("personal_user")

        loadProfile()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.listrequest -> {
                val intent = Intent(context, RequestListActivity::class.java)
                startActivity(intent)
            }
            R.id.caridonor -> {
                val intent = Intent(context, FindDonorActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun loadProfile() {
        val user = auth.currentUser
        val userref = user?.uid?.let { databaseReference?.child(it) }

        userref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val name = snapshot.child("name").value.toString()
                val delim = " "

                val split = name.split(delim)
                val getFirstName = split[0]

                binding.title.text = context?.resources?.getString(R.string.firstname, getFirstName)

                Glide.with(requireContext())
                    .load(snapshot.child("profileimageurl").value.toString())
                    .apply(RequestOptions().override(50,50))
                    .centerCrop()
                    .into(binding.imgProfile)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}