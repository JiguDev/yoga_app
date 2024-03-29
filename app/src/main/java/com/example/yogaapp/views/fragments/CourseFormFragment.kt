package com.example.yogaapp.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.yogaapp.R
import com.example.yogaapp.databinding.FragmentCourseFormBinding

class CourseFormFragment : Fragment() {

    private var _binding: FragmentCourseFormBinding? = null
    private val UI get() = _binding!!

    private val args : CourseFormFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_course_form, container, false)
        return UI.root
    }
}