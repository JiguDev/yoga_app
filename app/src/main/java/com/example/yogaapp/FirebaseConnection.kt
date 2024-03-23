package com.example.yogaapp

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val TAG = "TAG"

class FirebaseConnection {
    
    fun saveData(data: String) {
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue(data)
    }
}