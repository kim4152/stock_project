package com.project.stockproject.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class ViewPagerSharedPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("viewPager33", Context.MODE_PRIVATE)

    fun getPosition(key: String): Int {
        return prefs.getInt(key, 0)
    }

    fun setPosition(key: String, str: Int) {
        prefs.edit().putInt(key, str).apply()
    }
}