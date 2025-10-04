package com.pill_mate.pill_mate_android.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Button

object KeyboardUtil {
    fun handleKeyboardVisibility(rootView: View, button: Button, defaultMargin: Int) {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
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
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setupHideKeyboardOnTouch(rootView: View, context: Context) {
        rootView.setOnTouchListener { v, _ ->
            hideKeyboard(context, v)
            v.clearFocus()
            false
        }
    }

}