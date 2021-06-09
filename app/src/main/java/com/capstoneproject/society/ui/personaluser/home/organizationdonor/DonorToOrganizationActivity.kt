package com.capstoneproject.society.ui.personaluser.home.organizationdonor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstoneproject.society.R

class DonorToOrganizationActivity : AppCompatActivity() {

    private fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_to_organization)

        setActionBarTitle(getString(R.string.title_donorToOrganization))
    }
}