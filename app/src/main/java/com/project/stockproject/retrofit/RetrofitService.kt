package com.project.stockproject.retrofit

import com.project.stockproject.common.GetToken
import com.project.stockproject.home.MFItem
import com.project.stockproject.home.Predic
import com.project.stockproject.search.AwsAPIStockInfo
import com.project.stockproject.stockInform.StockInformItem
import com.project.stockproject.stockInform.StockList
import com.project.stockproject.stockInform.chart.GetCurrentChart
import com.project.stockproject.stockInform.chart.PredictionData
import com.project.stockproject.stockInform.disclosure.CorpCode
import com.project.stockproject.stockInform.openai.ChatCompletionResponse
import com.project.stockproject.stockInform.openai.ChatRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.Query

interface RetrofitService {

    //한국투자증권 토큰발급
    @POST("ndAWS-1/getToken")
    fun getToken(): Call<GetToken>

    //검색창
    @GET("ndAWS-1/getInfoByName")
    fun search1(@Query("stock_name") stock_name:String):Call<List<AwsAPIStockInfo>>


    //종목 정보
    @GET("uapi/domestic-stock/v1/quotations/inquire-price?")
    fun stockInform(
        @Query("fid_cond_mrkt_div_code") fid_cond_mrkt_div_code: String,
        @Query("fid_input_iscd") fid_input_iscd: String,
    ): Call<StockInformItem>

    //한투증(일/주/월)
    @GET("uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice?")
    fun getCurrentChart(
        @Query("fid_cond_mrkt_div_code") fid_cond_mrkt_div_code: String,
        @Query("fid_input_iscd") fid_input_iscd: String  ,
        @Query("fid_input_date_1") fid_input_date_1: String ,  //앞 날짜
        @Query("fid_input_date_2") fid_input_date_2: String  , //뒤 날짜
        @Query("fid_period_div_code") fid_period_div_code:String  ,
        @Query("fid_org_adj_prc") fid_org_adj_prc:String  ,
    ): Call<GetCurrentChart>

    //open ai
    @POST("v1/chat/completions")
    fun getOpenAI(
        @Body chatRequest: ChatRequest
    ): Call<ChatCompletionResponse>

    //주가 예측 (선차트)
    @GET("getStockInfo")
    fun getStockInfo(@Query("stock_code")stockCode: String,
                     @Query("kospi_kosdaq") marketCode:String): Call<PredictionData>

    //증시 코드 받아오기
    @GET("ndAWS-1/getCorp")
    fun getCorpCode(@Query("stock_code") stockCode:String, ): Call<CorpCode>

    //멀티 팩터 포토폴리오
    @GET("/ndAWS-1/get-multi-factor")
    fun getMultiFactor():Call<List<MFItem>>

    @GET("ndAWS-1/getInfoByCode")
    fun getStockNameByCode(@Query("stock_code") stock_code:String):Call<List<AwsAPIStockInfo>>

    //예측가 상위 n개
    @GET("ndAWS-1/get-predic")
    fun getPredic(@Query("limit") limit:String): Call<List<Predic>>
}