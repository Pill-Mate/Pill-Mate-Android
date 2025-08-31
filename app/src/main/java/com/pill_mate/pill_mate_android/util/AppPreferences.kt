package com.pill_mate.pill_mate_android.util

import android.content.Context

object AppPreferences {
    private const val PREF_NAME = "pill_mate_app_prefs"
    private const val KEY_SKIP_WARNING_DIALOG = "skip_polypharmacy_dialog"
    private const val KEY_SKIP_WARNING_TIMESTAMP = "skip_warning_timestamp"

    // "3일 동안 안 보기"로 타임스탬프 저장
    fun setSkipWarningDialogForDays(context: Context, skip: Boolean, days: Int = 3) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        if (skip) {
            val expireTime = System.currentTimeMillis() + days * 24 * 60 * 60 * 1000L
            editor.putLong(KEY_SKIP_WARNING_TIMESTAMP, expireTime)
        } else {
            editor.remove(KEY_SKIP_WARNING_TIMESTAMP)
        }
        editor.apply()
    }

    // 3일 안에 skip 상태인지 확인
    fun shouldShowWarningDialog(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val expireTime = prefs.getLong(KEY_SKIP_WARNING_TIMESTAMP, 0L)
        return System.currentTimeMillis() > expireTime
    }

    fun setSkipWarningDialog(context: Context, skip: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SKIP_WARNING_DIALOG, skip).apply()
    }
}