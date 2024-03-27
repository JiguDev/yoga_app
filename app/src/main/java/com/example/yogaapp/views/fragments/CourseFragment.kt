package com.example.yogaapp.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.yogaapp.R
import com.example.yogaapp.adapters.CourseRecyclerViewAdapter
import com.example.yogaapp.databinding.FragmentCourseBinding
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CourseFragment : Fragment() {

    private var _binding: FragmentCourseBinding? = null
    private val UI get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_course, container, false)

        UI.recyclerView.adapter = CourseRecyclerViewAdapter()
        val flexboxLayoutManager = FlexboxLayoutManager(context)
           flexboxLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        UI.recyclerView.layoutManager = flexboxLayoutManager

        return UI.root
    }
}