package com.project.stockproject.stockInform.tabFragment


import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.stockproject.common.MyApplication.getPackageManager
import com.project.stockproject.common.MyApplication.startActivity
import com.project.stockproject.retrofit.RetrofitFactory
import com.project.stockproject.retrofit.RetrofitService
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.net.URISyntaxException


class TabViewModel : ViewModel() {
    val newsRetrofit: RetrofitService = RetrofitFactory.newsRetrofit.create(RetrofitService::class.java)

    fun getChart(stockCode:String):MutableLiveData<MutableList<CandleDTO>>{
        var liveData : MutableLiveData<MutableList<CandleDTO>> = MutableLiveData()
        NaverChart(liveData,1,stockCode).start()
        return liveData
    }

    class NaverChart(
        private val liveData: MutableLiveData<MutableList<CandleDTO>> = MutableLiveData(),
        private var getPage: Int,
        private val code: String,
    ) : Thread() {
        override fun run() {
            var isEnd = false
            var candleDTO: MutableList<CandleDTO> = mutableListOf()
            var cnt: Int = 0
            while (!isEnd) {
                val url = "https://finance.naver.com/item/sise_day.nhn?code=$code&page=$getPage"
                try {
                    val document = Jsoup.connect(url).get()

                    // parse
                    val trList = document.select("tr[onmouseover=mouseOver(this)]")

                    for (tr in trList) {
                        val tdList = tr.select("td")

                        var dateString = tdList[0].text().trim().replace(".", "-")
                        val closePrice = tdList[1].text().replace(",", "").toFloat()  // 종가
                        val diffPrice = tdList[2].text().replace(",", "").toFloat()  // 전일비
                        val openPrice = tdList[3].text().replace(",", "").toFloat()  // 시가
                        val highPrice = tdList[4].text().replace(",", "").toFloat()  // 고가
                        val lowPrice = tdList[5].text().replace(",", "").toFloat()  // 저가
                        val volume = tdList[6].text().replace(",", "").toFloat()  // 거래량


                        var tmpList: MutableList<PriceDTO> = mutableListOf()
                        tmpList.add(PriceDTO(highPrice, lowPrice, openPrice, closePrice, diffPrice, volume))
                        candleDTO.add(CandleDTO(dateString, tmpList))
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                //isEnd=true
                getPage++
                cnt++
                if (cnt > 2) {
                    isEnd = true
                }
            }
            liveData.postValue(candleDTO)
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    fun getNews(stockName:String):MutableLiveData<List<NewsModel>>{
        var liveData:MutableLiveData<List<NewsModel>> = MutableLiveData()
        newsRetrofit.getNews(stockName).enqueue(object :Callback<NewsRSS>{
            override fun onResponse(call: Call<NewsRSS>, response: Response<NewsRSS>) {
                response.body()?.channel
                val list = response.body()?.channel?.items.orEmpty().transform()
                liveData.postValue(list)
            }

            override fun onFailure(call: Call<NewsRSS>, t: Throwable) {
            }

        })
        return liveData
    }
}