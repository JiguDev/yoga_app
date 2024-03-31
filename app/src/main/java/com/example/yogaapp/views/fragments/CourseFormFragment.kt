package com.example.yogaapp.views.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.yogaapp.R
import com.example.yogaapp.databinding.FragmentCourseFormBinding
import com.example.yogaapp.helpers.setSpinnerAdapter
import com.example.yogaapp.models.Course
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

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

    private lateinit var storageRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_course_form, container, false)

        storageRef = Firebase.storage.reference;

        val gson = Gson()

        val course = gson.fromJson(args.ref, Course::class.java)

        Log.d(TAG, "onCreateView: ${course}")

        val courseTitleEditText = UI.courseTitleEditText
        val courseDescriptionEditText = UI.courseDescriptionEditText
        val courseDetailsEditText = UI.courseDetailsEditText
        val courseLevelSpinner = UI.courseLevelSpinner
        val courseActiveSpinner = UI.courseActiveSpinner
        val courseImageCardView = UI.courseImageCardView
        val saveCourseButton = UI.saveCourseButton

        courseActiveSpinner.setSpinnerAdapter(listOf(SELECT_ACTIVE_OR_NOT, YES, NO))
        courseLevelSpinner.setSpinnerAdapter(listOf(SELECT_LEVEL, BEGINNER, INTERMEDIATE, ADVANCED))

        setValues(course)

        courseImageCardView.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)
        }

        saveCourseButton.setOnClickListener {
            val courseTitle = courseTitleEditText.text.toString()
            val courseDescription = courseDescriptionEditText.text.toString()
            val courseDetails = courseDetailsEditText.text.toString()
            val courseLevel = courseLevelSpinner.selectedItem.toString()
            val isCourseCompleted = courseActiveSpinner.selectedItem.toString() == YES
            val img = "https://www.example.com/image.jpg"
            val course = Course(
                courseTitle = courseTitle,
                courseDescription = courseDescription,
                courseDetails = courseDetails,
                courseLevel = courseLevel,
                img = img,
                isCourseCompleted = isCourseCompleted
            )
            storeCourseInFirebase(course)
        }

        return UI.root
    }

    fun storeCourseInFirebase(course: Course) {
        val ref = Firebase.database.getReference("courses")
        val key = ref.push().key
        key?.let {
            course.courseId = key
            ref.child(it).setValue(course).addOnCompleteListener {
                Log.d("gfx", "storeCourseInFirebase: ${it.isSuccessful}")
            }.addOnFailureListener {
                Log.d("gfx", "storeCourseInFirebase: ${it.message}")
            }
        }
    }

    //function to set values with null check
    fun setValues(course: Course) {
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
        UI.courseActiveSpinner.setSelection(
            when (course.isCourseCompleted) {
                true -> if (course.courseTitle == null) 0 else 1
                false -> if (course.courseTitle == null) 0 else 2
                else -> 0
            }
        )
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                val imageUri: Uri? = result.data?.data
                //Log.d(TAG, "$imageUri")
                uploadImage(imageUri)
            }
        }

    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.run { getColumnIndex(OpenableColumns.DISPLAY_NAME) })
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun uploadImage(imageUri: Uri?) {
        val sd = getFileName(requireContext(), imageUri!!)
        val uploadTask = storageRef.child("file/$sd").putFile(imageUri)
        uploadTask.addOnSuccessListener {
            storageRef.child("upload/$sd").downloadUrl.addOnSuccessListener {
                Glide.with(requireActivity())
                    .load(it)
                    .into(UI.courseImageView)
                Log.d("gfx", "download passed")
            }.addOnFailureListener {
                Log.d("gfx", "${it.message}")
            }
        }.addOnFailureListener {
            Log.d("gfx", "${it.message}")
        }
    }

}