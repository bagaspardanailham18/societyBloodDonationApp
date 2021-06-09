package com.capstoneproject.society.ui.personaluser.home.requestlist

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.capstoneproject.society.R
import com.google.firebase.auth.FirebaseUser
import java.lang.IllegalStateException

class MyDialog(user: FirebaseUser?, uidRequest: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val alertDialog = AlertDialog.Builder(it)
            alertDialog.setView(requireActivity().layoutInflater.inflate(R.layout.donate_dialog, null))
            alertDialog.setPositiveButton("Submit", DialogInterface.OnClickListener({dialog, it ->

            }))

            alertDialog.create()
        }?:throw IllegalStateException("Activity is null")
    }
}