package com.example.randomtext

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "random_text_prefs"
        private const val KEY_ENABLED = "notifications_enabled"
        private const val KEY_HOUR = "scheduled_hour"
        private const val KEY_MINUTE = "scheduled_minute"
        private const val DEFAULT_HOUR = 9
        private const val DEFAULT_MINUTE = 0
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var hour: Int
        get() = prefs.getInt(KEY_HOUR, DEFAULT_HOUR)
        set(value) = prefs.edit().putInt(KEY_HOUR, value).apply()

    var minute: Int
        get() = prefs.getInt(KEY_MINUTE, DEFAULT_MINUTE)
        set(value) = prefs.edit().putInt(KEY_MINUTE, value).apply()
}
