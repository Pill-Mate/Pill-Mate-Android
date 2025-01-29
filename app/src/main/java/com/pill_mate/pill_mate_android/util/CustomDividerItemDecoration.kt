package com.pill_mate.pill_mate_android.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomDividerItemDecoration(
    private val dividerHeight: Float,
    private val dividerColor: Int,
    private val marginStart: Float = 0f,
    private val marginEnd: Float = 0f
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        color = dividerColor
        style = Paint.Style.FILL
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val totalChildCount = parent.adapter?.itemCount ?: 0
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position < totalChildCount - 1) { // 마지막 아이템 제외
                val params = child.layoutParams as RecyclerView.LayoutParams
                val left = parent.paddingLeft + marginStart
                val right = parent.width - parent.paddingRight - marginEnd
                val top = child.bottom + params.bottomMargin
                val bottom = top + dividerHeight

                canvas.drawRect(left, top.toFloat(), right, bottom, paint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        if (position < itemCount - 1) { // 마지막 아이템 제외
            outRect.bottom = dividerHeight.toInt()
        } else {
            outRect.bottom = 0
        }
    }
}