package com.rorpheeyah.androidkotlinbaseproject.base

import androidx.fragment.app.Fragment
import com.rorpheeyah.androidkotlinbaseproject.util.extensions.TransitionAnimation

interface NavigationListener {

    fun navigateTo(
        fragment: Fragment,
        backStackTag: String,
        animationType: TransitionAnimation,
        isAdd: Boolean
    )

}