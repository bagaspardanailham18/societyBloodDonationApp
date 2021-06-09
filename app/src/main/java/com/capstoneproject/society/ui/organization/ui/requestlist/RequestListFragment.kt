package com.capstoneproject.society.ui.organization.ui.requestlist

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.FragmentOrgRequestBinding
import com.capstoneproject.society.model.RequestItems
import com.capstoneproject.society.ui.personaluser.home.requestlist.RequestListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RequestListFragment : Fragment() {

    private var _binding: FragmentOrgRequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestList: MutableList<RequestItems>
    private lateinit var listRequest: RecyclerView

    private lateinit var ref: DatabaseReference

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrgRequestBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val idUser = user?.uid

        listRequest = RecyclerView(requireContext())

        ref = FirebaseDatabase.getInstance().getReference("request_blood")

        requestList = mutableListOf()

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
                    binding.rvRequest.layoutManager = LinearLayoutManager(activity)
                    binding.rvRequest.adapter = adapter
                    adapter.notifyDataSetChanged()
                }else {
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
        val intent = Intent(requireContext(), DetailRequestOrgActivity::class.java).apply {
            putExtra(DetailRequestOrgActivity.EXTRA_DETAIL, data)
        }
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
    }

}