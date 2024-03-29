package com.example.yogaapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import com.example.yogaapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

}