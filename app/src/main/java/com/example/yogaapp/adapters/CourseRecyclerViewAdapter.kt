package com.example.yogaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.databinding.CourseListItemBinding
import com.example.yogaapp.views.fragments.CourseFragmentDirections

class CourseRecyclerViewAdapter : RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder>() {

    private var courseList: List<String> =
        listOf(
            "Course 1",
            "Course 2",
            "Course 3",
            "Course 4",
            "Course 5",
            "Course 6",
            "Course 7",
            "Course 8",
            "Course 9",
            "Course 10"
        )

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
            UI.name.text = item
        } else {
            UI.name.visibility = View.GONE
            UI.image.visibility = View.GONE
            UI.addImg.visibility = View.VISIBLE
        }
        UI.card.setOnClickListener {
            onPressListener?.invoke(item)
            //navigate to course form

            it.findNavController().navigate(CourseFragmentDirections.actionCourseFragmentToCourseFormFragment(item))

        }
    }

    private var onPressListener: ((String) -> Unit)? = null
    fun setOnPressListener(listener: (String) -> Unit) {
        onPressListener = listener
    }
}