package com.capstoneproject.society.ui.auth.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager)
        binding.viewPagerRegister.adapter = sectionPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPagerRegister)
    }
}