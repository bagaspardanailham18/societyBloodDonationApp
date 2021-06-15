package com.capstoneproject.society.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.capstoneproject.society.R
import com.capstoneproject.society.firebase.FirebaseService
import com.capstoneproject.society.ui.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//        ))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = Firebase.auth

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
//        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userId")
        Firebase.messaging.subscribeToTopic("/topics/$userId")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}