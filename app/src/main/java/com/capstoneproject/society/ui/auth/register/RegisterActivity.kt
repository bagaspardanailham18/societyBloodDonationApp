package com.capstoneproject.society.ui.auth.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.custom_toolbar_2)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.custom_toolbar_2_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitle.setText(getString(R.string.title_register))

        val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager)
        binding.viewPagerRegister.adapter = sectionPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPagerRegister)
    }
}