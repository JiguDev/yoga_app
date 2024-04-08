package com.example.yogaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.databinding.CourseListItemBinding
import com.example.yogaapp.databinding.NoficationListItemBinding
import com.example.yogaapp.models.Notification

private const val TAG = "gfx"
class UserListRecyclerViewAdapter : RecyclerView.Adapter<UserListRecyclerViewAdapter.ViewHolder>() {


    private var list: List<Notification> =
        listOf()

    class ViewHolder(val binding: NoficationListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            NoficationListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val UI = holder.binding
        val item = list[position]
        UI.title.text = item.title
        //Log.d(TAG, "onBindViewHolder: $gsReference")
    }

    private var onPressListener: ((Notification) -> Unit)? = null
    fun setOnPressListener(listener: (Notification) -> Unit) {
        onPressListener = listener
    }

    fun setList(courseList: List<Notification>) {
        this.list = courseList
        notifyDataSetChanged()
    }
}