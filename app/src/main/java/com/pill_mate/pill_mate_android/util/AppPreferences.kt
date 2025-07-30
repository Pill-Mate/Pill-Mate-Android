package com.pill_mate.pill_mate_android.util

import android.content.Context

object AppPreferences {
    private const val PREF_NAME = "pill_mate_app_prefs"
    private const val KEY_SKIP_WARNING_DIALOG = "skip_polypharmacy_dialog"

    fun setSkipWarningDialog(context: Context, skip: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SKIP_WARNING_DIALOG, skip).apply()
    }

    fun shouldShowWarningDialog(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return !prefs.getBoolean(KEY_SKIP_WARNING_DIALOG, false)
    }
}