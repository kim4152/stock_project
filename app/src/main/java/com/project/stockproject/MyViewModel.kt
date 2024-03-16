package com.project.stockproject

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.project.stockproject.common.GetIPv4
import com.project.stockproject.common.GetToken
import com.project.stockproject.common.InfoCheck
import com.project.stockproject.common.MyApplication
import com.project.stockproject.home.MFItem
import com.project.stockproject.home.MajorIndexViewPagerDTO
import com.project.stockproject.home.Predic
import com.project.stockproject.retrofit.RetrofitFactory
import com.project.stockproject.retrofit.RetrofitService
import com.project.stockproject.room.FavoriteDB
import com.project.stockproject.room.FolderTable
import com.project.stockproject.room.ItemTable
import com.project.stockproject.search.AwsAPIStockInfo
import com.project.stockproject.search.Search
import com.project.stockproject.stockInform.StockInformItem
import com.project.stockproject.stockInform.StockList
import com.project.stockproject.stockInform.StockOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class MyViewModel : ViewModel() {
    companion object {
        lateinit var getSearchOnclick: Search
    }


    // ConnectivityLiveData 인스턴스를 ViewModel 내에서 관리
    private val connectivityLiveData = ConnectivityLiveData(MyApplication.getAppContext())

    // 네트워크 상태를 외부에 노출
    fun getConnectivityLiveData() = connectivityLiveData

    private val awsAPIRetrofit: RetrofitService =
        RetrofitFactory.awsAPIRetrofit.create(RetrofitService::class.java)

    //한국투자증권 토큰 발급
    fun getToken(): LiveData<String> {
        val liveData: MutableLiveData<String> = MutableLiveData()
        awsAPIRetrofit.getToken().enqueue(object : Callback<GetToken> {
            override fun onResponse(call: Call<GetToken>, response: Response<GetToken>) {
                val result = response.body()?.body?.accessToken
                liveData.postValue(result)
            }

            override fun onFailure(call: Call<GetToken>, t: Throwable) {
                t
            }
        })
        return liveData
    }

    //ipv4 얻기
    fun getIpv4():MutableLiveData<String>{
        var liveData = MutableLiveData<String>()
        awsAPIRetrofit.getIpv4().enqueue(object : Callback<GetIPv4>{
            override fun onResponse(call: Call<GetIPv4>, response: Response<GetIPv4>) {
                liveData.postValue(response.body()?.body)
            }

            override fun onFailure(call: Call<GetIPv4>, t: Throwable) {
            }
        })
        return liveData
    }

    //알림 체크
    fun infoCheck():MutableLiveData<String>{
        var liveData = MutableLiveData<String>()
        awsAPIRetrofit.getInfo().enqueue(object :Callback<InfoCheck>{
            override fun onResponse(call: Call<InfoCheck>, response: Response<InfoCheck>) {
                liveData.postValue(response.body()?.body)
            }

            override fun onFailure(call: Call<InfoCheck>, t: Throwable) {

            }

        })
        return liveData
    }

    //검색
    private var searchCall: Call<List<AwsAPIStockInfo>>? = null
    fun search(stockName: String): MutableLiveData<List<AwsAPIStockInfo>> {

        var liveData: MutableLiveData<List<AwsAPIStockInfo>> = MutableLiveData()
        searchCall = awsAPIRetrofit.search1(stockName)

        searchCall?.enqueue(object : Callback<List<AwsAPIStockInfo>> {
            override fun onResponse(
                call: Call<List<AwsAPIStockInfo>>,
                response: Response<List<AwsAPIStockInfo>>
            ) {
                liveData.postValue(response.body())
            }

            override fun onFailure(call: Call<List<AwsAPIStockInfo>>, t: Throwable) {
            }

        })

        return liveData
    }

    fun cancelSearch() {
        searchCall?.cancel()
    } //검색 스레드 제거

    //주요지수
    fun getMajorIndexViewPager(mutableListOf: MutableList<String>): MutableLiveData<List<MajorIndexViewPagerDTO>> {
        var liveData: MutableLiveData<List<MajorIndexViewPagerDTO>> = MutableLiveData()

        MajorstockIndex(mutableListOf, liveData).start()

        return liveData
    }

    class MajorstockIndex(
        private val mutableListOf: MutableList<String>,
        private val liveData: MutableLiveData<List<MajorIndexViewPagerDTO>>
    ) : Thread() {
        private val historicalPrices: MutableList<MajorIndexViewPagerDTO> = mutableListOf()

        //크롤링한 날짜 stringToLocalDate
        /*fun dateFormat(d: String): LocalDate {
            val formattedDate = d.replace('-', '.')
            val (yyyy, mm, dd) = formattedDate.split('.').map { it.toInt() }
            return LocalDate.of(yyyy, mm, dd)
        }*/

        override fun run() {
            try {
                mutableListOf.forEach {
                    //리스트에 있는 종목 하나씩 크롤링. (KOSPI,KOSDQA,KOSPI200)
                    majorstockCrawling(it)
                }
                //MajorstockCrawling(it) 이 함수에서 저장한 historicalPrices 리스트 liveData에 저장
                liveData.postValue(historicalPrices)

            } catch (e: Exception) {

            }
        }

        private fun majorstockCrawling(name: String) {
            val url = "https://finance.naver.com/sise/sise_index_day.nhn?code=$name&page=1"

            val source = URL(url).readText()
            val doc = Jsoup.parse(source)

            val date = doc.select("td.date")
            val price = doc.select("td.number_1")
            val rateDown = doc.select("td.rate_down")

            for (n in date.indices) {

                if (date[n].text().split('.')[0].toIntOrNull() != null) {

                    val thisDate = date[n].text()
                    // val formattedDate = dateFormat(thisDate)    //날짜

                    val presentPrice = price[n * 4].text()     //가격
                    val rateDown = rateDown[0].text()  //전일비
                    val fluctuationRate = price[(n * 4) + 1].text()
                    historicalPrices.add(
                        MajorIndexViewPagerDTO(
                            name,
                            thisDate,
                            presentPrice,
                            rateDown,
                            fluctuationRate
                        )
                    )
                } else {
                    historicalPrices.add(MajorIndexViewPagerDTO("", "", "", "", ""))
                }
                break //가장 최근 날짜만 조회하고 for문 break
            }
        }
    }


    //검색 결과 클릭
    fun setSearchOnclik(search: Search) {
        getSearchOnclick = search

    }

    //종목정보 기본조회
    private val stockInformRetrofit: RetrofitService =
        RetrofitFactory.stockInfromRetrofit.create(RetrofitService::class.java)

    fun stockInformRetrofit(stockList: List<StockList>): MutableLiveData<List<StockOutput>> {
        val responseList = MutableLiveData<List<StockOutput>>()

        viewModelScope.launch {
            val tmpList = mutableListOf<StockOutput>()

            stockList.forEach { it ->
                try {
                    val response = withContext(Dispatchers.IO) {
                        stockInformRetrofit.stockInform("J", it.stockCode).execute()
                    }

                    if (response.isSuccessful) {
                        response.body()?.output?.let { a ->
                            tmpList.add(a)
                        }
                    } else {
                        // Handle unsuccessful response if needed
                    }
                } catch (e: Exception) {
                    // Handle exceptions if needed
                }
            }

            responseList.value = tmpList
        }

        return responseList
    }



    //개별종목보회
    fun stockInform(stockCode: String): MutableLiveData<StockOutput> {
        val liveData: MutableLiveData<StockOutput> = MutableLiveData()
        stockInformRetrofit.stockInform("J", stockCode).enqueue(object : Callback<StockInformItem> {
            override fun onResponse(
                call: Call<StockInformItem>,
                response: Response<StockInformItem>
            ) {
                val a = response.body()?.output
                liveData.postValue(response.body()?.output)
            }

            override fun onFailure(call: Call<StockInformItem>, t: Throwable) {
            }

        })
        return liveData
    }

    //tabLayout처음 그리기
    fun setTabLayout(stockCode: String): MutableLiveData<StockOutput> {
        var list: MutableLiveData<StockOutput> = MutableLiveData()
        stockInformRetrofit.stockInform("J", stockCode)
            .enqueue(object : Callback<StockInformItem> {
                override fun onResponse(
                    call: Call<StockInformItem>,
                    response: Response<StockInformItem>
                ) {
                    list.postValue(response.body()?.output)
                }

                override fun onFailure(call: Call<StockInformItem>, t: Throwable) {
                }
            })
        return list
    }

    fun getMultiFactor(): MutableLiveData<List<MFItem>> {
        var liveData: MutableLiveData<List<MFItem>> = MutableLiveData()
        awsAPIRetrofit.getMultiFactor().enqueue(object : Callback<List<MFItem>> {
            override fun onResponse(call: Call<List<MFItem>>, response: Response<List<MFItem>>) {
                liveData.postValue(response.body())
            }

            override fun onFailure(call: Call<List<MFItem>>, t: Throwable) {

            }

        })
        return liveData
    }

    fun getStockNameByCode(stockCode: String): MutableLiveData<String> {
        var liveData = MutableLiveData<String>()
        awsAPIRetrofit.getStockNameByCode(stockCode)
            .enqueue(object : Callback<List<AwsAPIStockInfo>> {
                override fun onResponse(
                    call: Call<List<AwsAPIStockInfo>>,
                    response: Response<List<AwsAPIStockInfo>>
                ) {
                    liveData.postValue(response.body()?.get(0)?.stock_name ?: null)
                }

                override fun onFailure(call: Call<List<AwsAPIStockInfo>>, t: Throwable) {
                }

            })
        return liveData
    }
    // 상위 예측가 얻기
    fun topPredic(limit:String):MutableLiveData<List<Predic>>{
        var liveData = MutableLiveData<List<Predic>>()
        awsAPIRetrofit.getPredic(limit).enqueue(object:Callback<List<Predic>>{
            override fun onResponse(call: Call<List<Predic>>, response: Response<List<Predic>>) {
                liveData.postValue(response.body())
            }

            override fun onFailure(call: Call<List<Predic>>, t: Throwable) {
            }

        })
        return liveData
    }

    //개별 종목 예측가
    fun getPredicEach(stockCode: String):MutableLiveData<Predic>{
        var liveData = MutableLiveData<Predic>()
        awsAPIRetrofit.getPredicEach(stockCode).enqueue(object : Callback<List<Predic>>{
            override fun onResponse(call: Call<List<Predic>>, response: Response<List<Predic>>) {
                liveData.postValue(response.body()?.get(0) ?: null)
            }

            override fun onFailure(call: Call<List<Predic>>, t: Throwable) {

            }

        })
        return liveData
    }
///////////////////////////////////////////////////////////////////////////
    //즐겨찾기 폴더 추가

    private val db =
        Room.databaseBuilder(MyApplication.getAppContext(), FavoriteDB::class.java, "favorite7")
            //.addMigrations(MIGRATION_3_4)
            .build()

    private val _addFolderResult = MutableLiveData<String>()
    val addFolderResult: LiveData<String> get() = _addFolderResult
    fun addFolder(order: Int, folderName: String, context: Context) {
        Thread {
            var index = db.folderDAO().getMaxOrder()
            if (index == null) {
                index = 0
            } else {
                index
            }
            val folderTable = FolderTable(0, folderName, index)
            db.folderDAO().insertFolder(folderTable)
            _addFolderResult.postValue("end")
        }.start()
    }

    //폴더 가져오기
    val _getAll = MutableLiveData<List<FolderTable>>()
    val getAllResult: LiveData<List<FolderTable>> get() = _getAll
    fun getAll() {
        Thread {
            _getAll.postValue(db.folderDAO().getAll())
        }.start()
    }

    fun count(): MutableLiveData<Int> {
        val liveData: MutableLiveData<Int> = MutableLiveData()
        Thread {
            val i = db.folderDAO().count()
            liveData.postValue(i)
        }.start()
        return liveData
    }

    //폴더 삭제
    private val _folderDeleteResult = MutableLiveData<String>()
    val folderDeleteResult: LiveData<String> get() = _folderDeleteResult
    fun folderDelete(list: List<String>) {
        Thread {
            db.folderDAO().folderDelete(list)
            _folderDeleteResult.postValue("end")
        }.start()
    }

    //아이템 삭제
    private val _itemDelete = MutableLiveData<String>()
    val itemDeleteResult: LiveData<String> get() = _itemDelete
    fun itemDelete(folderName: String, list: List<String>) {

        Thread {
            db.itemDAO().itemDelete(folderName, list)
            _itemDelete.postValue("end")
        }.start()
    }

    //폴더 개수 체크
    private val _checkTextView = MutableLiveData<Int>()
    val checkTextViewResult: LiveData<Int> get() = _checkTextView
    fun checkTextView(folderName: String) {
        Thread {
            val int = db.folderDAO().checkTextView(folderName)
            _checkTextView.postValue(int)
        }.start()
    }

    //해당 폴더에 있는 아이템 수 조회
    fun countItem(folderName: String?): MutableLiveData<LiveData<Int>> {
        val countItem: MutableLiveData<LiveData<Int>> = MutableLiveData()
        Thread {
            val int = folderName?.let { db.itemDAO().count(it) }
            countItem.postValue(int)
        }.start()
        return countItem
    }

    /////////////////////////////////////////////////////////////////////
    fun insertItem(stockName: String, folderName: String, stockCode: String) {
        Thread {
            var index = db.itemDAO().getMaxOrder()
            if (index == null) {
                index = 0
            } else {
                index
            }

            db.itemDAO().insertItem(
                ItemTable(
                    itemName = stockName,
                    folderName = folderName,
                    index = index,
                    itemCode = stockCode
                )
            )
        }.start()
    }

    //해당 폴더에 있는 아이템 조회
    private val _getAllItems = MutableLiveData<List<ItemTable>>()
    val getAllItemsResult: LiveData<List<ItemTable>> get() = _getAllItems
    fun getAllItems(folderName: String) {
        Log.d("dafdf","@@@@@@@@@@@@")
        Thread {
            _getAllItems.postValue(db.itemDAO().getAll(folderName))
        }.start()
    }


    //폴더 이름 변경
    fun reNameFolder(oldName: String, newName: String): MutableLiveData<String> {
        val liveData: MutableLiveData<String> = MutableLiveData()
        Thread {
            db.folderDAO().updateFolderName(oldName, newName)
            liveData.postValue("finish")
        }.start()
        return liveData
    }

    //동일 종목 체크
    fun itemCheck(folderName: String?, stockName: List<String>): MutableLiveData<Int> {
        val liveData: MutableLiveData<Int> = MutableLiveData()
        Thread {
            if (folderName != null) {
                val int = db.itemDAO().checkItem(folderName, stockName)
                liveData.postValue(int)
            }
        }.start()
        return liveData
    }

    //itemMove
    fun itemMove(folderName: String, stockName: String, index: Int) {
        Thread {
            db.itemDAO().updateOrder(folderName, stockName, index)
        }.start()
    }
///////////////////////////////////////////
}
