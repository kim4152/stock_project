package com.project.stockproject.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SubRetrofitFactory {
    var AWS_EC2_URL="http://52.78.159.169:5001/"

    //ai 예상 이동편균선 그리기
    fun initializeRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AWS_EC2_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}