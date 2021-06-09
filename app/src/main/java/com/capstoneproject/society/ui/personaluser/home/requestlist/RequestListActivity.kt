package com.capstoneproject.society.ui.personaluser.home.requestlist

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ActivityRequestListBinding
import com.capstoneproject.society.model.RequestItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RequestListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestListBinding

    private lateinit var requestList: MutableList<RequestItems>
    private lateinit var listRequest: RecyclerView
    private lateinit var ref: DatabaseReference

    private lateinit var auth: FirebaseAuth

    private fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onResume() {
        super.onResume()
        val bloodTypes = resources.getStringArray(R.array.blood_type_filter)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_bloodtype_item, bloodTypes)
        binding.spinnerBloodtypeFilter.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBarTitle(getString(R.string.title_request_list))

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val idUser = user?.uid

        listRequest = RecyclerView(this)

        ref = FirebaseDatabase.getInstance().getReference("request_blood")

        requestList = mutableListOf()

        binding.spinnerBloodtypeFilter.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (idUser != null) {
                    showFilterData(idUser)
                }
            }

        }

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    requestList.clear()
                    showLoading(false)
                    showNotFound(false)
                    for (h in snapshot.children) {
                        if (h.key == idUser.toString()) {
                            continue
                        }
                        val request = h.getValue(RequestItems::class.java)
                        if (request != null) {
                            requestList.add(request)
                        }
                    }
                    val adapter = RequestListAdapter(requestList, object : RequestListAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: RequestItems) {
                            moveToDetailRequestActivity(data)
                        }
                    })
                    listRequest.adapter = adapter
                    binding.rvRequest.layoutManager = LinearLayoutManager(this@RequestListActivity)
                    binding.rvRequest.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    showNotFound(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        binding.rvRequest.setHasFixedSize(true)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showNotFound(states: Boolean) {
        if (states) {
            binding.notFound.visibility = View.VISIBLE
        } else {
            binding.notFound.visibility = View.GONE
        }
    }

    private fun moveToDetailRequestActivity(data: RequestItems) {
        val intent = Intent(this, DetailRequestActivity::class.java).apply {
            putExtra(DetailRequestActivity.EXTRA_DETAIL, data)
        }
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun showFilterData(idUser: String) {
        val bloodType = binding.spinnerBloodtypeFilter.text.toString()
        showLoading(true)

        if (bloodType == "All") {
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        requestList.clear()
                        showLoading(false)
                        showNotFound(false)
                        for (h in snapshot.children) {
                            if (h.key == idUser.toString()) {
                                continue
                            }
                            val request = h.getValue(RequestItems::class.java)
                            if (request != null) {
                                requestList.add(request)
                            }
                        }
                        val adapter = RequestListAdapter(requestList, object : RequestListAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: RequestItems) {
                                moveToDetailRequestActivity(data)
                            }
                        })
                        listRequest.adapter = adapter
                        binding.rvRequest.layoutManager = LinearLayoutManager(this@RequestListActivity)
                        binding.rvRequest.adapter = adapter
                        adapter.notifyDataSetChanged()
                    } else {
                        showNotFound(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        } else {
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        requestList.clear()
                        showLoading(false)
                        showNotFound(false)
                        for (h in snapshot.children) {
                            if (h.key == idUser.toString()) {
                                continue
                            } else if (!h.child("bloodtyperequest").value!!.equals(bloodType)) {
                                Log.d("cek", "Lewat")
                                continue
                            }
                            val request = h.getValue(RequestItems::class.java)
                            if (request != null) {
                                requestList.add(request)
                                Log.d("cek", "Jumlah = ${requestList.size}")
                            }
                        }
                        val adapter = RequestListAdapter(requestList, object : RequestListAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: RequestItems) {
                                moveToDetailRequestActivity(data)
                            }
                        })
                        listRequest.adapter = adapter
                        binding.rvRequest.layoutManager = LinearLayoutManager(this@RequestListActivity)
                        binding.rvRequest.adapter = adapter
                        adapter.notifyDataSetChanged()
                    } else {
                        showNotFound(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}