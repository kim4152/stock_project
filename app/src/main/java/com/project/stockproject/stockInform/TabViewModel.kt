package com.project.stockproject.stockInform


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.project.stockproject.retrofit.RetrofitFactory
import com.project.stockproject.retrofit.RetrofitService
import com.project.stockproject.search.AwsAPIStockInfo
import com.project.stockproject.stockInform.chart.CurrentAdditional
import com.project.stockproject.stockInform.chart.CurrentChart
import com.project.stockproject.stockInform.chart.GetCurrentChart
import com.project.stockproject.stockInform.chart.PredictionData
import com.project.stockproject.stockInform.chart.PriceDTO
import com.project.stockproject.stockInform.chart.StockInfoRequest
import com.project.stockproject.stockInform.openai.ChatCompletionResponse
import com.project.stockproject.stockInform.openai.ChatRequest
import com.project.stockproject.stockInform.openai.FocusAreasResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TabViewModel : ViewModel() {
    private val newsRetrofit: RetrofitService = RetrofitFactory.newsRetrofit.create(RetrofitService::class.java)
    private val kisRetrofit: RetrofitService = RetrofitFactory.stockChartRetrofit.create(RetrofitService::class.java)
    private val awsEC2Retrofit: RetrofitService = RetrofitFactory.awsEC2Retrofit.create(RetrofitService::class.java)
    /////현재 차트 불러오기

    fun getCurrentChart(stockCode:String,startDate:String,endDate:String):MutableLiveData<MutableList<CurrentChart>>{
        var liveData : MutableLiveData<MutableList<CurrentChart>> = MutableLiveData()
        kisRetrofit.getCurrentChart("J",stockCode,startDate,endDate,"D","1").enqueue(object : Callback<GetCurrentChart>{
            override fun onResponse(call: Call<GetCurrentChart>, response: Response<GetCurrentChart>) {
                val tmpList  = mutableListOf<CurrentChart>()
                response.body()?.output2?.forEach {
                    if (it.stck_bsop_date!=null){
                        tmpList.add(
                            CurrentChart(
                                it.stck_bsop_date,
                                PriceDTO(it.stck_clpr.toFloat(),
                                    it.stck_oprc.toFloat(),
                                    it.stck_hgpr.toFloat(),
                                    it.stck_lwpr.toFloat(),
                                    it.acml_vol,
                                    it.prdy_vrss_sign,
                                    it.prdy_vrss),
                                CurrentAdditional(it.acml_vol,it.prdy_vrss_sign,it.prdy_vrss)
                            )
                        )
                    }
                }
                liveData.postValue(tmpList)
            }

            override fun onFailure(call: Call<GetCurrentChart>, t: Throwable) {
            }
            })
            return liveData
        }

    ///////////////////////////////////////////////////////////////////////////


    private val openAIRetrofit: RetrofitService = RetrofitFactory.openAIRetrofit.create(RetrofitService::class.java)
    val openAILiveData:MutableLiveData<List<String>> = MutableLiveData()
    fun openAI(chatRequest: ChatRequest):MutableLiveData<List<String>>{

        openAIRetrofit.getOpenAI(chatRequest).enqueue(object : Callback<ChatCompletionResponse>{
            override fun onResponse(
                call: Call<ChatCompletionResponse>,
                response: Response<ChatCompletionResponse>
            ) {
                val jsonString=response.body()?.choices?.get(0)?.message?.content
                val focusAreasResponse: FocusAreasResponse = Gson().fromJson(jsonString, FocusAreasResponse::class.java)
                //val value=focusAreasResponse.focusAreas //string json을 리스트로
                //openAILiveData.postValue(value)
                Log.d("dadf","value=>"+focusAreasResponse.toString())
            }

            override fun onFailure(call: Call<ChatCompletionResponse>, t: Throwable) {}

        })
        return openAILiveData
    }

    private  var getPredicCall: Call<PredictionData> ?= null
     fun getPredicChart(stockInfoRequest: StockInfoRequest):MutableLiveData<PredictionData>{
         val liveData: MutableLiveData<PredictionData> = MutableLiveData()
         getPredicCall = awsEC2Retrofit.getStockInfo(stockInfoRequest)
        getPredicCall?.enqueue(object : Callback<PredictionData>{
            override fun onResponse(
                call: Call<PredictionData>,
                response: Response<PredictionData>
            ) {
                val result=response.body()
                liveData.postValue(result)
            }
            override fun onFailure(call: Call<PredictionData>, t: Throwable) {}

        })
         return  liveData
    }

    fun cancelPredicCall(){
        getPredicCall?.cancel()
    }
}