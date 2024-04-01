package com.example.yogaapp.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.yogaapp.R
import com.example.yogaapp.databinding.FragmentCourseFormBinding
import com.example.yogaapp.helpers.setSpinnerAdapter
import com.example.yogaapp.models.Course
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlin.math.log

private const val YES = "Yes"

private const val NO = "No"

private const val BEGINNER = "Beginner"

private const val INTERMEDIATE = "Intermediate"

private const val ADVANCED = "Advanced"

private const val SELECT_LEVEL = "Select Level"

private const val SELECT_ACTIVE_OR_NOT = "Select Active or Not"

class CourseFormFragment : Fragment() {

    private var _binding: FragmentCourseFormBinding? = null
    private val UI get() = _binding!!

    private val args: CourseFormFragmentArgs by navArgs()

    var course = Course()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_course_form, container, false)

        val gson = Gson()

        course = gson.fromJson(args.ref, Course::class.java)

        val courseTitleEditText = UI.courseTitleEditText
        val courseDescriptionEditText = UI.courseDescriptionEditText
        val courseDetailsEditText = UI.courseDetailsEditText
        val courseLevelSpinner = UI.courseLevelSpinner
        val courseActiveSpinner = UI.courseActiveSpinner
        val courseImageCardView = UI.courseImageCardView
        val saveCourseButton = UI.saveCourseButton

        courseActiveSpinner.setSpinnerAdapter(listOf(SELECT_ACTIVE_OR_NOT, YES, NO))
        courseLevelSpinner.setSpinnerAdapter(listOf(SELECT_LEVEL, BEGINNER, INTERMEDIATE, ADVANCED))

        setValuesToUI(course)

        courseImageCardView.setOnClickListener {

        }

        saveCourseButton.setOnClickListener {

            val courseTitle = courseTitleEditText.text.toString()
            val courseDescription = courseDescriptionEditText.text.toString()
            val courseDetails = courseDetailsEditText.text.toString()
            val courseLevel = courseLevelSpinner.selectedItem.toString()
            val courseCompleted = when (courseActiveSpinner.selectedItem) {
                YES -> true
                NO ->  false
                else ->  null
            }
            val img = "https://www.example.com/image.jpg"

            course.courseTitle = courseTitle.trim()
            course.courseDescription = courseDescription.trim()
            course.courseDetails = courseDetails.trim()
            course.courseLevel = courseLevel
            course.courseCompleted = courseCompleted
            course.img = img

            val ref = Firebase.database.getReference("courses")
            if(validateCourseValues(course)){
                storeCourseInFirebase(course, ref)
            }
            else{
                Snackbar.make(UI.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        return UI.root
    }

    fun storeCourseInFirebase(
        course: Course, ref: DatabaseReference
    ) {
        Log.d(TAG, "storeCourseInFirebase: ${course.courseId}")
        if (course.courseId == null) {
            val key = ref.push().key
            key?.let {
                course.courseId = key
            }
        }
        ref.child(course.courseId!!).setValue(course).addOnCompleteListener {
            // Log.d("gfx", "storeCourseInFirebase: ${it.isSuccessful}")
            findNavController().popBackStack()
        }.addOnFailureListener {
            //Log.d("gfx", "storeCourseInFirebase: ${it.message}")
        }
    }

    //function to set values with null check
    fun setValuesToUI(course: Course) {
        UI.courseTitleEditText.setText(course.courseTitle)
        UI.courseDescriptionEditText.setText(course.courseDescription)
        UI.courseDetailsEditText.setText(course.courseDetails)
        UI.courseLevelSpinner.setSelection(
            when (course.courseLevel) {
                BEGINNER -> 1
                INTERMEDIATE -> 2
                ADVANCED -> 3
                else -> 0
            }
        )
        Log.d(TAG, "setValuesToUI: ${course.courseCompleted}")
        UI.courseActiveSpinner.setSelection(
            when (course.courseCompleted) {
                true -> 1
                false -> 2
                else -> 0
            }
        )
    }
    //function to validate course values
    fun validateCourseValues(course: Course): Boolean {
        return course.courseTitle != null
                && course.courseTitle != ""
                && course.courseDescription != null
                && course.courseDescription != ""
                && course.courseDetails != null
                && course.courseDetails != ""
                && course.courseLevel != null
                && course.courseLevel != SELECT_LEVEL
                && course.courseCompleted != null
    }
}