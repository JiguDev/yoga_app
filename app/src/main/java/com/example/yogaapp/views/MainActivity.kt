package com.example.yogaapp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.yogaapp.R
import com.example.yogaapp.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "gfx"

class MainActivity : AppCompatActivity() {

    private lateinit var UI: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UI= DataBindingUtil.setContentView(this,R.layout.activity_main);

        UI.alert.setOnClickListener {
            toggleAlert()
        }

        UI.exit.setOnClickListener {
            signOut()
        }

        UI.lifecycleOwner = this

        checkFirebaseAuth()

    }

    private fun checkFirebaseAuth() {
        val auth = Firebase.auth
        if (auth.currentUser != null) {
            // User is signed in
        } else {
            // No user is signed in
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        checkFirebaseAuth()
    }

    private fun toggleAlert() {
        UI.notificationFragment.visibility =
            if (UI.notificationFragment.visibility == View.VISIBLE) {
                UI.navHostFragment.visibility = View.VISIBLE
                View.GONE
            } else {
                UI.navHostFragment.visibility = View.GONE
                View.VISIBLE
            }
    }

}