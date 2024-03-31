package com.example.yogaapp.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.R
import com.example.yogaapp.adapters.CourseRecyclerViewAdapter
import com.example.yogaapp.databinding.FragmentCourseBinding
import com.example.yogaapp.models.Course
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val TAG = "gfx"

class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private val UI get() = _binding!!

    private lateinit var recyclerView : RecyclerView
    private lateinit var progressIndicator : CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_course, container, false)

        recyclerView = UI.recyclerView
        progressIndicator = UI.progressIndicator

        val courseRecyclerViewAdapter = CourseRecyclerViewAdapter()
        recyclerView.adapter = courseRecyclerViewAdapter
        val flexboxLayoutManager = FlexboxLayoutManager(context)
        flexboxLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        recyclerView.layoutManager = flexboxLayoutManager

        courseRecyclerViewAdapter.setOnPressListener {

        }
        getCourseListFromFirebase(courseRecyclerViewAdapter)
        return UI.root
    }

    fun getCourseListFromFirebase(courseRecyclerViewAdapter: CourseRecyclerViewAdapter) {
        loading(true)
        val ref = Firebase.database.getReference("courses")
        ref.get().addOnSuccessListener {
            val courseList = it.children.map { it.getValue(Course::class.java) }
            courseRecyclerViewAdapter.setCourseList(courseList as List<Course>)
            loading(false)
        }.addOnFailureListener {
            Log.d(TAG, "getCourseListFromFirebase: ${it.message}")
            Snackbar.make(UI.root, "Failed to get course list", Snackbar.LENGTH_LONG).show()
            loading(false)
        }
    }

    fun loading(toLoad: Boolean){
        if (toLoad) {
            progressIndicator.show()
            recyclerView.visibility = View.GONE
        } else {
            progressIndicator.hide()
            recyclerView.visibility = View.VISIBLE
        }
    }
}