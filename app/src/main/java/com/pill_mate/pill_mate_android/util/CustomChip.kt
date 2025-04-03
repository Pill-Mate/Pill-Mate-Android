package com.pill_mate.pill_mate_android.util

import android.content.Context
import android.widget.TextView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.pill_mate.pill_mate_android.R

object CustomChip {

    fun createChip(context: Context, text: String): TextView {
        return TextView(context).apply {
            this.text = text
            setPadding(12, 4, 12, 4)
            background = ContextCompat.getDrawable(context, R.drawable.bg_tag_main_blue_2_radius_4)
            setTextAppearance(R.style.TagTextStyle)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 6
            layoutParams = params
        }
    }

    fun createDosingTextView(context: Context): TextView {
        return TextView(context).apply {
            text = context.getString(R.string.five_when_taking)
            setPadding(8, 0, 8, 0)
            setTextAppearance(R.style.TagTextStyle)
            setTextColor(ContextCompat.getColor(context, R.color.gray_2))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }
    }
}