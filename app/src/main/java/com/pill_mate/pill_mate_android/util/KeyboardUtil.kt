package com.pill_mate.pill_mate_android.util

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button

object KeyboardUtil {
    fun handleKeyboardVisibility(rootView: View, button: Button, defaultMargin: Int) {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.height
                val keypadHeight = screenHeight - rect.bottom

                val params = button.layoutParams as ViewGroup.MarginLayoutParams

                rootView.post {
                    if (keypadHeight > screenHeight * 0.15) {
                        params.bottomMargin = keypadHeight + 16
                    } else {
                        params.bottomMargin = defaultMargin
                    }
                    button.layoutParams = params
                }
            }
        })
    }
}