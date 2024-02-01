package com.project.stockproject.common

import android.app.Application
import android.content.Context
import android.widget.Toast

object MyApplication : Application() {
    lateinit var context : Context

     fun onCreate1(context: Context) {
        super.onCreate()
        MyApplication.context = context
    }


    fun getAppContext() : Context {
        return context
    }
    fun makeToast(string: String){
        Toast.makeText(context,string,Toast.LENGTH_SHORT).show()
    }
}