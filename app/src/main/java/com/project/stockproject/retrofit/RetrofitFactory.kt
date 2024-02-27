package com.project.stockproject.retrofit



import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private const val BASE_URL = "https://apis.data.go.kr/"
    private const val KIS_BASE_URL="https://openapi.koreainvestment.com:9443/"
    var TOKEN  =""
    private const val  APP_KEY ="PSUzp1CHZpjgiCVInHoU5vXrc1gMvQ34FbRw"
    private const val  SECRETE_APP_KEY ="YXuAAaw6KiVKA3I2Q2jOWh/VnrBiWYRq7YJogtIP4xC0jcq+uoPML2EnCQtvxVk5O9GbPp3GrFmwG+Q7a8XFH0PdnRHN9j4DIdN2p7lPc/krfgQcwp5QAYuHLxFQLKR396TcIhsLm+sdCi7JBCf+7rOpLCPLpvaaHeZRhpT90zC0a8dD+IA="
    private const val GOOGLENEWS = "https://news.google.com/"
    // Retrofit 인스턴스를 만들 때 OkHttpClient에 Interceptor 추가
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(MyInterceptor()) // 여러 개의 헤더를 추가하는 Interceptor를 등록
        .build()
    val okHttpClient2 = OkHttpClient.Builder()
        .addInterceptor(MyInterceptor2()) // 여러 개의 헤더를 추가하는 Interceptor를 등록
        .build()


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val stockInfromRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(KIS_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val stockChartRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(KIS_BASE_URL)
        .client(okHttpClient2)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //토큰 받아올떄
    private const val AWS_BASE_URL="https://9kmqg2j0tf.execute-api.ap-northeast-2.amazonaws.com/"
    val awsRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AWS_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //여러개의 헤더
    class MyInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest: Request = chain.request()

            // 여러 개의 헤더를 추가할 때
            val modifiedRequest: Request = originalRequest.newBuilder()
                .addHeader("Host","openapi.koreainvestment.com:9443")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer $TOKEN")
                .addHeader("appkey", APP_KEY)
                .addHeader("appsecret", SECRETE_APP_KEY)
                .addHeader("tr_id", "FHKST01010100")
                .build()

            return chain.proceed(modifiedRequest)
        }
    }
    class MyInterceptor2 : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest: Request = chain.request()

            // 여러 개의 헤더를 추가할 때
            val modifiedRequest: Request = originalRequest.newBuilder()
                .addHeader("content-type", "application/json; charset=utf-8")
                .addHeader("authorization", "Bearer $TOKEN")
                .addHeader("appkey", APP_KEY)
                .addHeader("appsecret", SECRETE_APP_KEY)
                .addHeader("tr_id", "FHKST03010100")
                .build()

            return chain.proceed(modifiedRequest)
        }
    }

    val newsRetrofit: Retrofit=Retrofit.Builder()
        .baseUrl(GOOGLENEWS)
        .addConverterFactory(
            TikXmlConverterFactory.create(
                TikXml.Builder()
                    .exceptionOnUnreadXml(false) //메핑이 안된 xml데이터가 있다면 exception을 날리지 마라
                    .build()
            )
        ).build()
}