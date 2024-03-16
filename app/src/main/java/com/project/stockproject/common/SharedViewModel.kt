package com.project.stockproject.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // 공유할 데이터를 MutableLiveData로 정의합니다.
    private val _sharedData = MutableLiveData<String>()
    val sharedData: LiveData<String> = _sharedData

    // 데이터를 업데이트하는 메서드를 정의합니다.
    fun updateData(data: String) {
        _sharedData.value = data
    }


    private val _checkToken = MutableLiveData<String>()
    val checkToken: LiveData<String> = _checkToken
    fun checkTokenOk(string:String){
        _checkToken.value=string
    }
}