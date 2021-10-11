package com.rorpheeyah.androidkotlinbaseproject.util.extensions

import androidx.fragment.app.FragmentTransaction
import com.rorpheeyah.androidkotlinbaseproject.R

enum class TransitionAnimation {
    FADE, SLIDE_UP, SLIDE_IN, NO_ANIMATION
}

fun FragmentTransaction.setCustomAnimation(type: TransitionAnimation) {
    when (type) {
        TransitionAnimation.SLIDE_IN -> setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
        TransitionAnimation.SLIDE_UP -> setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
        TransitionAnimation.FADE -> setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
        TransitionAnimation.NO_ANIMATION -> return
    }
}