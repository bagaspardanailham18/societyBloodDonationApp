package com.capstoneproject.society.ui.personaluser.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.society.databinding.FragmentNotificationsBinding
import com.capstoneproject.society.model.RequestAcceptedItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var requestAcceptedList: MutableList<RequestAcceptedItem>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(layoutInflater, container, false)
        val view = binding?.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestAcceptedList = mutableListOf()

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        val ref = FirebaseDatabase.getInstance().getReference("request_accepted")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (h in snapshot.children) {
                        if (!h.child("idPatient").value!!.equals(userId)) {
                            continue
                        }

                        val result = h.getValue(RequestAcceptedItem::class.java)
                        if (result != null) {
                            requestAcceptedList.add(result)
                        }
                    }
                    val adapter = NotificationAdapter(this@NotificationsFragment, requestAcceptedList, object : NotificationAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: RequestAcceptedItem) {
                            moveToDetailNotifActivity(data)
                        }
                    })
                    binding?.rvNotification?.adapter = adapter
                    binding?.rvNotification?.layoutManager = LinearLayoutManager(requireContext())
                    binding?.rvNotification?.setHasFixedSize(true)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun moveToDetailNotifActivity(data: RequestAcceptedItem) {
        val intent = Intent(requireContext(), NotificationDetailActivity::class.java)
        intent.putExtra(NotificationDetailActivity.EXTRA_DATA, data)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}