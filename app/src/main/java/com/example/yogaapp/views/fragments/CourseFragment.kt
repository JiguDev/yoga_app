package com.example.yogaapp.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.R
import com.example.yogaapp.adapters.CourseRecyclerViewAdapter
import com.example.yogaapp.databinding.FragmentCourseBinding
import com.example.yogaapp.models.Course
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

val TAG = "gfx"

class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private val UI get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: CircularProgressIndicator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_course, container, false)

        recyclerView = UI.recyclerView
        progressIndicator = UI.progressIndicator

        loading(true)

        val courseRecyclerViewAdapter = CourseRecyclerViewAdapter()
        recyclerView.adapter = courseRecyclerViewAdapter
        val flexboxLayoutManager = FlexboxLayoutManager(context)
        flexboxLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        recyclerView.layoutManager = flexboxLayoutManager

        courseRecyclerViewAdapter.setOnPressListener {
            //Log.d(TAG, "onCreateView: ${it}")
            val json = it.toJSON()
            //Log.d(TAG, "onCreateView: ${json}")
            findNavController().navigate(
                CourseFragmentDirections.actionCourseFragmentToCourseFormFragment(
                    json
                )
            )
        }

        getCourseListFromFirebase(courseRecyclerViewAdapter)
        return UI.root
    }

    fun getCourseListFromFirebase(courseRecyclerViewAdapter: CourseRecyclerViewAdapter) {
        val ref = Firebase.database.getReference("courses")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseList = snapshot.children.map { it.getValue(Course::class.java) }
                courseRecyclerViewAdapter.setCourseList(courseList as List<Course>)
                loading(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(UI.root, "Failed to get course list", Snackbar.LENGTH_LONG).show()
                loading(false)
            }
        })
//        ref.get().addOnSuccessListener {
//            val courseList = it.children.map { it.getValue(Course::class.java) }
//            //Log.d(TAG, "getCourseListFromFirebase: ${courseList}")
//            courseRecyclerViewAdapter.setCourseList(courseList as List<Course>)
//            loading(false)
//        }.addOnFailureListener {
//            Snackbar.make(UI.root, "Failed to get course list", Snackbar.LENGTH_LONG).show()
//            loading(false)
//        }
    }

    fun loading(toLoad: Boolean) {
        if (toLoad) {
            progressIndicator.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            progressIndicator.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}