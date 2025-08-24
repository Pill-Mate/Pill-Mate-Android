package com.pill_mate.pill_mate_android.util

import android.text.InputFilter
import android.widget.EditText

fun EditText.setMinMaxIntegerValue(min: Int, max: Int) {
    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        try {
            val newInput = dest.toString().substring(0, dstart) +
                    source.subSequence(start, end) +
                    dest.toString().substring(dend)

            if (newInput.isEmpty()) return@InputFilter null

            val value = newInput.toInt()
            if (value in min..max) null else ""
        } catch (e: NumberFormatException) {
            ""
        }
    }

    filters = arrayOf(filter)
}

fun EditText.setMinMaxDecimalValue(min: Float, max: Float, decimalLimit: Int = 2) {
    this.keyListener = android.text.method.DigitsKeyListener.getInstance("0123456789.")

    val filter = InputFilter { source, start, end, dest, dstart, dend ->
        try {
            val newInput = StringBuilder(dest).apply {
                replace(dstart, dend, source.subSequence(start, end).toString())
            }.toString()

            if (newInput.isEmpty() || newInput == ".") {
                return@InputFilter null
            }

            val value = newInput.toFloat()

            // 범위 체크
            if (value !in min..max) return@InputFilter ""

            // 소수 자릿수 제한
            if (newInput.contains(".")) {
                val decimalPart = newInput.substringAfter(".", "")
                if (decimalPart.length > decimalLimit) return@InputFilter ""
            }

            null
        } catch (e: NumberFormatException) {
            "" // 숫자가 아닐 경우 입력 차단
        }
    }

    this.filters = arrayOf(filter)
}