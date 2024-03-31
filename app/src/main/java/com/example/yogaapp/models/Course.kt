package com.example.yogaapp.models

import com.google.gson.Gson

data class Course(
    var courseId: String? = null,
    val courseTitle: String? = null,
    val courseDescription: String? = null,
    val courseDetails: String? = null,
    val courseLevel: String? = null,
    val img: String? = null,
    val isCourseCompleted: Boolean? = null
){
    fun toJSON():String{
        val gson = Gson()
        return gson.toJson(this)
    }
}