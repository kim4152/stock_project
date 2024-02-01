package com.project.stockproject.common

import android.app.Activity
import android.widget.Toast

class BackKeyHandler(private val activity : Activity?) {
    private var backKeyPressedTime: Long = 0

    fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity!!.finish()
        }
    }
}