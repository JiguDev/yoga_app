package com.example.yogaapp.views.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

    //

    //

    private var _binding: FragmentCourseFormBinding? = null
    private val UI get() = _binding!!

    private val args: CourseFormFragmentArgs by navArgs()

    var course = Course()

    var fileUri: Uri? = null

    lateinit var ref: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_course_form, container, false)

        val gson = Gson()
        course = gson.fromJson(args.ref, Course::class.java)

        ref = Firebase.database.getReference("courses")

        makeKey()

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
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(
                    intent, "Select Picture"
                ),
                1
            )
        }

        saveCourseButton.setOnClickListener {

            val courseTitle = courseTitleEditText.text.toString()
            val courseDescription = courseDescriptionEditText.text.toString()
            val courseDetails = courseDetailsEditText.text.toString()
            val courseLevel = courseLevelSpinner.selectedItem.toString()
            val courseCompleted = when (courseActiveSpinner.selectedItem) {
                YES -> true
                NO -> false
                else -> null
            }

            course.courseTitle = courseTitle.trim()
            course.courseDescription = courseDescription.trim()
            course.courseDetails = courseDetails.trim()
            course.courseLevel = courseLevel
            course.courseCompleted = courseCompleted

            if (validateCourseValues(course)) {
                uploadImageToFirebaseStorage()
            } else {
                Snackbar.make(UI.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
            }
        }
        return UI.root
    }

    fun storeCourseInFirebase() {
        ref.child(course.courseId!!).setValue(course).addOnCompleteListener {
            findNavController().popBackStack()
        }.addOnFailureListener {
            showLoading(false)
            Snackbar.make(UI.root, "Failed to save course", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun makeKey() {
        if (course.courseId == null) {
            val key = ref.push().key
            key?.let {
                course.courseId = key
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileUri)
                UI.courseImageDisplayView.setImageBitmap(bitmap)
                UI.courseImageDisplayViewCard.visibility = View.VISIBLE
                course.img = fileUri!!.getFileTypeExtension()
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }
    }

    fun uploadImageToFirebaseStorage(): Unit {
        if (course.courseId == null || fileUri == null || course.courseId == "") {
            return
        }
        if (fileUri != null) {
            showLoading(true)
            val ref: StorageReference =
                Firebase.storage.getReference().child("images/${course.courseId}")
            ref.putFile(fileUri!!)
                .addOnSuccessListener {
                    Snackbar.make(UI.root, "Image uploaded successfully", Snackbar.LENGTH_SHORT)
                        .show()
                    storeCourseInFirebase()
                }.addOnFailureListener {
                    Snackbar.make(
                        UI.root,
                        "Failed to upload image : ${it.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
        }
    }

    fun Uri.getFileTypeExtension(): String {
        val cr = requireActivity().contentResolver
        val mime = cr.getType(this)
        return mime?.substring(mime.indexOf("/") + 1) ?: ""
    }

    fun showLoading(toLoad: Boolean) {
        if (toLoad) {
            UI.courseImageUploadProgressIndicator.show()
            UI.saveCourseButton.hide()
        } else {
            UI.courseImageUploadProgressIndicator.hide()
            UI.saveCourseButton.show()
        }
    }
}