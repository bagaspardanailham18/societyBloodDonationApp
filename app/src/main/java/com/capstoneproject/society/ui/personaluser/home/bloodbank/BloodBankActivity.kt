package com.capstoneproject.society.ui.personaluser.home.bloodbank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstoneproject.society.R

class BloodBankActivity : AppCompatActivity() {

    private fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blood_bank)

        setActionBarTitle(getString(R.string.title_bloodbank))
    }
}