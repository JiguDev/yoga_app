package com.example.yogaapp.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yogaapp.R
import com.example.yogaapp.adapters.UserListRecyclerViewAdapter
import com.example.yogaapp.databinding.FragmentNotificationBinding
import com.example.yogaapp.models.Course
import com.example.yogaapp.models.Notification
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val UI get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val ref = Firebase.database.getReference("addCourses")

        val notificationsAdapter = UserListRecyclerViewAdapter()
        UI.notificationRecyclerView.adapter = notificationsAdapter

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = snapshot.children.map { it.getValue(Notification::class.java) }
                Log.d(TAG, "onDataChange: $notifications")
                notificationsAdapter.setList(notifications as List<Notification>)
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(UI.root, "Failed to get course list", Snackbar.LENGTH_LONG).show()
            }
        })
        return UI.root
    }
}