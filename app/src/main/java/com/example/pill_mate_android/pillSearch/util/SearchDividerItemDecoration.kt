package com.example.pill_mate_android.pillSearch.util

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class SearchDividerItemDecoration(
    private val color: Int,
    private val height: Float // 선의 두께 (픽셀 단위)
) : RecyclerView.ItemDecoration() {
    private val paint = Paint()

    init {
        paint.color = color // 색상 설정
        paint.strokeWidth = height // 두께 설정
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + height

            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}