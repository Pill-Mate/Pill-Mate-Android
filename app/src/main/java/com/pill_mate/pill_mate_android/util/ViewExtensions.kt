package com.pill_mate.pill_mate_android.util

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View

// 터치 영역 넓히기
fun View.expandTouchArea(extraPadding: Int) {
    val parentView = this.parent as? View ?: return
    parentView.post {
        val rect = Rect()
        this.getHitRect(rect)
        rect.top -= extraPadding
        rect.bottom += extraPadding
        rect.left -= extraPadding
        rect.right += extraPadding
        parentView.touchDelegate = TouchDelegate(rect, this)
    }
}