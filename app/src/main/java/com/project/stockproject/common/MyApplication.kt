package com.project.stockproject.common

import android.app.Application
import android.content.Context
import android.util.Log
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
    fun makeLog(string:String){
        Log.d("aaabbb",string)
    }
}