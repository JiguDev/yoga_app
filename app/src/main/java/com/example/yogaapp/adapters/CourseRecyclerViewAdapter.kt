package com.example.yogaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.databinding.CourseListItemBinding
import com.example.yogaapp.models.Course
import com.example.yogaapp.views.fragments.CourseFragmentDirections

class CourseRecyclerViewAdapter : RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder>() {

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
        }
        UI.card.setOnClickListener {
            onPressListener?.invoke(item)
        }
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
}