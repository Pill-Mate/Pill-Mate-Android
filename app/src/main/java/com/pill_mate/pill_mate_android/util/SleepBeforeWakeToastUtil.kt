package com.pill_mate.pill_mate_android.util

import android.app.Activity
import android.content.res.ColorStateList
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.pill_mate.pill_mate_android.R
import com.pill_mate.pill_mate_android.databinding.SnackbarCustomBinding

object SleepBeforeWakeToastUtil {

    private const val DEFAULT_MSG = "취침시간은 기상시간보다 늦어야 합니다"

    private fun toMinutes(hour12: Int, minute: Int, amPmIndex: Int): Int {
        val isPm = amPmIndex == 1         // 0=오전, 1=오후
        var h = hour12 % 12               // 12 → 0
        if (isPm) h += 12
        return h * 60 + minute
    }

    fun toMinutesFromPickers(
        hourPicker: NumberPicker,
        minutePicker: NumberPicker,
        amPmPicker: NumberPicker,
        minuteDisplayedValues: Array<String>? = null
    ): Int {
        val min = minuteDisplayedValues?.get(minutePicker.value)?.toIntOrNull() ?: minutePicker.value
        return toMinutes(hourPicker.value, min, amPmPicker.value)
    }

    fun showBottomToast(
        activity: Activity,
        message: String = DEFAULT_MSG,
        iconRes: Int = R.drawable.ic_warning,
    ) {
        val vb = SnackbarCustomBinding.inflate(activity.layoutInflater)
        vb.tvContent.text = message
        vb.ivIcon.setImageResource(iconRes)
        Toast(activity).apply {
            duration = Toast.LENGTH_SHORT
            view = vb.root
            setGravity(
                android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL, 0, 100
            )
            show()
        }
    }

    fun setEnabledStyle(
        button: TextView,
        enabled: Boolean,
        @ColorRes enabledBg: Int,
        @ColorRes disabledBg: Int,
        @ColorRes enabledText: Int,
        @ColorRes disabledText: Int
    ) {
        button.isEnabled = enabled
        val ctx = button.context
        val bg = ContextCompat.getColor(ctx, if (enabled) enabledBg else disabledBg)
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(bg))
        val tc = ContextCompat.getColor(ctx, if (enabled) enabledText else disabledText)
        button.setTextColor(tc)
    }
}
