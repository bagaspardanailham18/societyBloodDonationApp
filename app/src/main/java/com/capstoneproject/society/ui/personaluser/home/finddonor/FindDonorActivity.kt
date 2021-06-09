package com.capstoneproject.society.ui.personaluser.home.finddonor

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ActivityFindDonorBinding
import com.capstoneproject.society.model.OrganizationSupplyItem
import com.capstoneproject.society.model.RequestItems
import com.capstoneproject.society.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindDonorActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityFindDonorBinding

    private lateinit var findDonorViewModel: FindDonorViewModel
    private lateinit var adapter: FindDonorAdapter

    private var supplies = arrayListOf<OrganizationSupplyItem>()

    private lateinit var ref: DatabaseReference

    private lateinit var auth: FirebaseAuth

    private fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    companion object {
        var databaseReference: DatabaseReference? = null
        var database: FirebaseDatabase? = null
    }

    override fun onResume() {
        super.onResume()
        val bloodTypes = resources.getStringArray(R.array.blood_type)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_bloodtype_item, bloodTypes)
        binding.spinnerBloodtypeRequest.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindDonorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBarTitle(getString(R.string.title_findDonor))

        binding.rvSupply.setHasFixedSize(true)
        findDonorViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(FindDonorViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val idUser = user?.uid

        ref = FirebaseDatabase.getInstance().getReference("request_blood")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (h in snapshot.children) {
                        if (h.key == idUser.toString()) {
                            binding.spinnerBloodtypeRequest.isEnabled = false
                            binding.btnRequest.setOnClickListener { view ->
                                Snackbar.make(view, "Anda harus menunggu proses request selesai untuk bisa melakukan request lagi!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                            }

                            binding.btnBroadcastToOtherUser.setOnClickListener { view ->
                                Snackbar.make(view, "Anda harus menunggu proses request selesai untuk bisa melakukan request lagi!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show()
                            }
                            return
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("personal_user")


        binding.btnRequest.setOnClickListener(this)
        binding.btnBroadcastToOtherUser.setOnClickListener(this)

        getSupplyData()
        showRecyclerList()

        onCLicked()
    }

    private fun onCLicked() {
        lifecycleScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                adapter.setOnItemClickCallback(object : FindDonorAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: OrganizationSupplyItem) {
                        moveToDetailSupplyActivity(data)
                    }
                })
            }
        }
    }

    private fun showRecyclerList() {
        binding.rvSupply.layoutManager = LinearLayoutManager(this)
        adapter = FindDonorAdapter(supplies)
        binding.rvSupply.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_request -> {
                showSupplyData()
            }
            R.id.btn_broadcast_to_other_user -> {
                broadcastToOtherUser()
            }
        }
    }

    private fun broadcastToOtherUser() {
        val user = auth.currentUser
        val userref = user?.uid?.let { databaseReference?.child(it) }
        ref = FirebaseDatabase.getInstance().getReference("request_blood").child(user?.uid!!)

        userref?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uid = user.uid
                val name = snapshot.child("name").value.toString()
                val phone = snapshot.child("phonenumber").value.toString()
                val city = snapshot.child("city").value.toString()
                val subdistrict = snapshot.child("subdistrict").value.toString()
                val bloodtyperequest = binding.spinnerBloodtypeRequest.text.toString()
                val usertype = snapshot.child("usertype").value.toString()
                val imageprofile = snapshot.child("profileimageurl").value.toString()

                val requestId = ref.push().key
                val request = requestId?.let { RequestItems(it, uid, name, phone, city, subdistrict, bloodtyperequest, usertype, imageprofile) }

                if (requestId != null) {
                    ref.setValue(request).addOnCompleteListener {
                        Toast.makeText(this@FindDonorActivity, "Request diproses", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@FindDonorActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showSupplyData() {
        val bloodType = binding.spinnerBloodtypeRequest.text.toString()
        if (bloodType.isEmpty()) return
        showLoading(true)
        findDonorViewModel.setSupply(bloodType, applicationContext)
    }

    private fun getSupplyData() {
        findDonorViewModel.getSupplies().observe(this, { supplyItems ->
            if (supplyItems != null) {
                adapter.setData(supplyItems)
                showLoading(false)
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

    private fun moveToDetailSupplyActivity(data: OrganizationSupplyItem) {
        val intent = Intent(this, DetailSupplyActivity::class.java).apply {
            putExtra(DetailSupplyActivity.EXTRA_DETAIL, data)
        }
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}