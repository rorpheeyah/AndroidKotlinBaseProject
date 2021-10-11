package com.rorpheeyah.androidkotlinbaseproject.base

import androidx.fragment.app.FragmentActivity

interface OnBackPressedListener {
    fun onBackPressed(activity: FragmentActivity) : Boolean
}