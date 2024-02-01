package com.project.stockproject.retrofit

import com.project.stockproject.search.Response
import com.project.stockproject.search.searchResponse
import com.project.stockproject.stockInform.StockInformItem
import com.project.stockproject.stockInform.tabFragment.NewsRSS
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

import retrofit2.http.Query

interface RetrofitService {

    //검색창
    @GET("1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo?")
    fun search(
        @Query("serviceKey") serviceKey : String,
        @Query("resultType") resultType : String,
        @Query("numOfRows") numOfRows : String,
        @Query("likeItmsNm") likeItmsNm: String,
        ): Call<searchResponse>
    //종목 정보


    @GET("uapi/domestic-stock/v1/quotations/inquire-price?")
    fun stockInform(
        @Query("fid_cond_mrkt_div_code") fid_cond_mrkt_div_code:String,
        @Query("fid_input_iscd") fid_input_iscd:String,
    ): Call<StockInformItem>

    @GET("rss/search?hl=ko&gl=KR&ceid=KR:ko")
    fun getNews(
        @Query("q") q:String
    ):Call<NewsRSS>


}