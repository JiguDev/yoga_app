package com.example.yogaapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Placeholder
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yogaapp.databinding.CourseListItemBinding
import com.example.yogaapp.models.Course
import com.example.yogaapp.views.fragments.CourseFragmentDirections
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val TAG = "gfx"

class CourseRecyclerViewAdapter : RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder>() {

    val storage = FirebaseStorage.getInstance()

    private var courseList: List<Course> =
        listOf()

    class ViewHolder(val binding: CourseListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            CourseListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = courseList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val UI = holder.binding
        val item = courseList[position]
        if (item != courseList.last()) {
            UI.name.text = item.courseTitle
        } else {
            UI.name.visibility = View.GONE
            UI.image.visibility = View.GONE
            UI.addImg.visibility = View.VISIBLE
            UI.placeholder.visibility = View.GONE
        }
        UI.card.setOnClickListener {
            onPressListener?.invoke(item)
        }
        val gsReference = storage.getReference().child("images/${item.courseId}")
        if (item.img != null) {
            loadImageFromFirebaseStorage(gsReference, UI.image,UI.placeholder)
        }
        //Log.d(TAG, "onBindViewHolder: $gsReference")
    }

    private var onPressListener: ((Course) -> Unit)? = null
    fun setOnPressListener(listener: (Course) -> Unit) {
        onPressListener = listener
    }
    fun setCourseList(list: List<Course>) {
        val temp = mutableListOf<Course>()
        temp.addAll(list)
        temp.add(Course(
            courseId = null,
            courseTitle = null,
            courseDescription = null,
            courseDetails = null,
            courseLevel = null,
            img = null,
            courseCompleted = null
        ))
        courseList = temp
        notifyDataSetChanged()
    }
    private fun loadImageFromFirebaseStorage(gsReference: StorageReference, image: ImageView,placeholder: ImageView) {
        gsReference.downloadUrl.addOnSuccessListener {
            Log.d(TAG, "loadImageFromFirebaseStorage: $it")
            Glide.with(image.context)
                .load(it)
                .into(image)
        }
            .addOnSuccessListener {
                placeholder.visibility = View.GONE
            }.addOnFailureListener {
            Log.d(TAG, "loadImageFromFirebaseStorage: ${it.message}")
        }
    }
}