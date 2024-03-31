package com.example.yogaapp.helpers

import android.R
import android.widget.ArrayAdapter
import android.widget.Spinner

fun Spinner.setSpinnerAdapter(list: List<String>) {
    val mAdapter =
        ArrayAdapter(
            context,
            R.layout.simple_spinner_item,
            list
        )
    mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    adapter = mAdapter
}