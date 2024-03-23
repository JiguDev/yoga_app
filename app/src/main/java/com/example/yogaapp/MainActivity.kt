package com.example.yogaapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseConnection = FirebaseConnection()

        val textView = findViewById<TextView>(R.id.textView)

        val editText = findViewById<TextView>(R.id.editText)

        val database = Firebase.database
        val location = database.getReference("message")

        location.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue()
                textView.text = "Firebase :" + value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                textView.text = "Failed to read value. ${error.toException()}"
            }

        })

        findViewById<Button>(R.id.button).setOnClickListener {
            val data = editText.text.toString()
           if (editText.text.toString().isNotEmpty()) {
                firebaseConnection.saveData(data)
                editText.text = ""
            }
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            signOut()
        }

        //startActivity(Intent(this, Login::class.java))

        checkFirebaseAuth()

    }
    private fun checkFirebaseAuth(){
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

    private fun signOut(){
        Firebase.auth.signOut()
        checkFirebaseAuth()
    }

}