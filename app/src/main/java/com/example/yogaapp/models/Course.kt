package com.example.yogaapp.models

import com.google.gson.Gson

data class Course(
    var courseId: String? = null,
    var courseTitle: String? = null,
    var courseDescription: String? = null,
    var courseDetails: String? = null,
    var courseLevel: String? = null,
    var img: String? = null,
    var courseCompleted: Boolean? = null,
    var active:Boolean? = false
){
    fun toJSON():String{
        val gson = Gson()
        return gson.toJson(this)
    }
}