package com.example.yogaapp.models

data class Course(
    val courseId: Int? = null,
    val courseTitle: String? = null,
    val courseDescription: String? = null,
    val courseDetails: String? = null,
    val courseLevel: String? = null,
    val img: String? = null,
    val isCourseCompleted: Boolean? = null
)