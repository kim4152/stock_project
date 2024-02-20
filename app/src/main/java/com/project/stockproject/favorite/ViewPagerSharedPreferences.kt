package com.project.stockproject.favorite

import android.content.Context
import android.content.SharedPreferences

class ViewPagerSharedPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getPosition(key: String): Int {
        return prefs.getInt(key, 0)
    }

    fun setPosition(key: String, str: Int) {
        prefs.edit().putInt(key, str).apply()
    }
}