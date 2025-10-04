package com.pill_mate.pill_mate_android

import android.view.View
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding

fun ViewBinding.showLoading() {
    root.findViewById<View>(R.id.progress)?.isVisible = true
}

fun ViewBinding.hideLoading() {
    root.findViewById<View>(R.id.progress)?.isVisible = false
}