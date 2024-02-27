package com.project.stockproject.retrofit

import com.project.stockproject.common.GetToken
import com.project.stockproject.search.Response
import com.project.stockproject.search.searchResponse
import com.project.stockproject.stockInform.StockInformItem
import com.project.stockproject.stockInform.tabFragment.GetCurrentChart
import com.project.stockproject.stockInform.tabFragment.NewsRSS
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

import retrofit2.http.Query

interface RetrofitService {

    //한국투자증권 토큰발급
    @POST("/getToken-API-V1/customer")
    fun getToken(): Call<GetToken>

    //검색창
    @GET("1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo?")
    fun search(
        @Query("serviceKey") serviceKey: String,
        @Query("resultType") resultType: String,
        @Query("numOfRows") numOfRows: String,
        @Query("likeItmsNm") likeItmsNm: String,
    ): Call<searchResponse>
    //종목 정보


    @GET("uapi/domestic-stock/v1/quotations/inquire-price?")
    fun stockInform(
        @Query("fid_cond_mrkt_div_code") fid_cond_mrkt_div_code: String,
        @Query("fid_input_iscd") fid_input_iscd: String,
    ): Call<StockInformItem>

    @GET("rss/search?hl=ko&gl=KR&ceid=KR:ko")
    fun getNews(
        @Query("q") q: String
    ): Call<NewsRSS>

    @GET("uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice?")
    fun getCurrentChart(
        @Query("fid_cond_mrkt_div_code") fid_cond_mrkt_div_code: String,
        @Query("fid_input_iscd") fid_input_iscd: String  ,
        @Query("fid_input_date_1") fid_input_date_1: String ,  //앞 날짜
        @Query("fid_input_date_2") fid_input_date_2: String  , //뒤 날짜
        @Query("fid_period_div_code") fid_period_div_code:String  ,
        @Query("fid_org_adj_prc") fid_org_adj_prc:String  ,
    ): Call<GetCurrentChart>
}