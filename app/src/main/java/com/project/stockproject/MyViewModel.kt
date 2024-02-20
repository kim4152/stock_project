package com.project.stockproject

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.project.stockproject.common.MyApplication
import com.project.stockproject.favorite.FavoriteDialogAdapter
import com.project.stockproject.home.MajorIndexViewPagerDTO
import com.project.stockproject.retrofit.RetrofitFactory
import com.project.stockproject.retrofit.RetrofitService
import com.project.stockproject.room.FavoriteDB

import com.project.stockproject.room.FavoriteDB.Companion.MIGRATION_3_4
import com.project.stockproject.room.FolderDAO
import com.project.stockproject.room.FolderTable
import com.project.stockproject.room.ItemTable
import com.project.stockproject.search.HistoryManager
import com.project.stockproject.search.Search
import com.project.stockproject.search.SearchHistoryManager
import com.project.stockproject.search.StockItem
import com.project.stockproject.search.searchResponse
import com.project.stockproject.stockInform.StockInformItem
import com.project.stockproject.stockInform.StockInformViewPagerAdapter
import com.project.stockproject.stockInform.StockOutput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import kotlin.math.abs

class MyViewModel : ViewModel() {
    companion object {
        private const val SERVICE_KEY =
            "9mMQezrKc2Qs061fZUnuMUTboZG85RptSH7RdZUS4jCe7fKxxALDPO0oNePEjJ4TGV9hj6Bo7Ce6ylMhxUr1fw=="
        lateinit var getSearchOnclick: Search
    }


    // ConnectivityLiveData 인스턴스를 ViewModel 내에서 관리
    private val connectivityLiveData = ConnectivityLiveData(MyApplication.getAppContext())

    // 네트워크 상태를 외부에 노출
    fun getConnectivityLiveData() = connectivityLiveData

    val retrofit: RetrofitService = RetrofitFactory.retrofit.create(RetrofitService::class.java)

    //검색
    private var searchCall: Call<searchResponse>? = null
    fun search(s: String): MutableLiveData<List<StockItem>> {

        var liveData: MutableLiveData<List<StockItem>> = MutableLiveData()
        searchCall = retrofit.search(SERVICE_KEY, "json", "10", s)

        searchCall?.enqueue(object : Callback<searchResponse> {
            override fun onResponse(
                call: Call<searchResponse>,
                response: retrofit2.Response<searchResponse>
            ) {
                liveData.postValue(response.body()?.response?.body?.items?.item)
            }

            override fun onFailure(call: Call<searchResponse>, t: Throwable) {
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
    val stockInformRetrofit: RetrofitService =
        RetrofitFactory.stockInfromRetrofit.create(RetrofitService::class.java)
    lateinit var viewPager2: ViewPager2
    lateinit var searchHistorylist: List<HistoryManager>

    //lateinit var searchHistoryManager:SearchHistoryManager
    fun stockInformRetrofit(
        searchHistorylist1: List<HistoryManager>,
        viewPager: ViewPager2,
        pagerAdapter: StockInformViewPagerAdapter
    ) {
        viewPager2 = viewPager
        searchHistorylist = searchHistorylist1
        // majorIndexViewPager(viewPager2)
        runBlocking {
            searchHistorylist.forEachIndexed { index, historyManager ->
                if (index == 0) return@forEachIndexed
                processStockInform(historyManager.stockCode, index, pagerAdapter)

            }
        }
    }

    val getList: MutableList<StockOutput> = mutableListOf()
    private fun processStockInform(
        stockCode: String,
        index: Int,
        pagerAdapter: StockInformViewPagerAdapter
    ) {
        stockInformRetrofit.stockInform("J", stockCode)
            .enqueue(object : Callback<StockInformItem> {
                override fun onResponse(
                    call: Call<StockInformItem>,
                    response: Response<StockInformItem>
                ) {

                    response.body()?.output?.let {
                        pagerAdapter.addItem(it)
                        getList.add(it)
                        viewPager2.offscreenPageLimit = index + 1
                    }
                }
                override fun onFailure(call: Call<StockInformItem>, t: Throwable) {
                }
            })
    }
    //개별종목보회
    fun stockInform(stockCode: String):MutableLiveData<StockOutput>{
        val liveData:MutableLiveData<StockOutput> = MutableLiveData()
        stockInformRetrofit.stockInform("J",stockCode).enqueue(object :Callback<StockInformItem>{
            override fun onResponse(
                call: Call<StockInformItem>,
                response: Response<StockInformItem>
            ) {
               liveData.postValue(response.body()?.output)
            }

            override fun onFailure(call: Call<StockInformItem>, t: Throwable) {
            }

        })
        return liveData
    }

    fun singleViewPager(stockCode: String, pagerAdapter: StockInformViewPagerAdapter) {
        processStockInform(stockCode, 0, pagerAdapter)
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
///////////////////////////////////////////////////////////////////////////
    //즐겨찾기 폴더 추가

    private val db = Room.databaseBuilder(MyApplication.getAppContext(),FavoriteDB::class.java,"favorite7")
        //.addMigrations(MIGRATION_3_4)
        .build()

    private val _addFolderResult = MutableLiveData<String>()
    val addFolderResult : LiveData<String> get() = _addFolderResult
    fun addFolder(order: Int, folderName: String, context: Context) {
        Thread{
            var index =db.folderDAO().getMaxOrder()
            if (index==null){
                index=0
            }else{
                index
            }
            val folderTable = FolderTable(0,folderName,index)
            db.folderDAO().insertFolder(folderTable)
            _addFolderResult.postValue("end")
        }.start()
    }

    //폴더 가져오기
    val _getAll = MutableLiveData<List<FolderTable>>()
    val getAllResult : LiveData<List<FolderTable>> get() = _getAll
    fun getAll() {
        Thread {
            _getAll.postValue(db.folderDAO().getAll())
        }.start()
    }
    fun count():MutableLiveData<Int>{
        val liveData:MutableLiveData<Int> = MutableLiveData()
        Thread{
            val i=db.folderDAO().count()
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
    fun itemDelete(folderName:String,list: List<String>) {

        Thread {
                db.itemDAO().itemDelete(folderName,list)
            _itemDelete.postValue("end")
        }.start()
    }
    //폴더 개수 체크
    private val _checkTextView = MutableLiveData<Int>()
    val checkTextViewResult:LiveData<Int> get() = _checkTextView
    fun checkTextView(folderName: String){
        Thread{
            val int = db.folderDAO().checkTextView(folderName)
            _checkTextView.postValue(int)
        }.start()
    }
    //해당 폴더에 있는 아이템 수 조회
    fun countItem(folderName: String?): MutableLiveData<LiveData<Int>>{
        val countItem : MutableLiveData<LiveData<Int>> = MutableLiveData()
        Thread{
            val int = folderName?.let { db.itemDAO().count(it) }
            countItem.postValue(int)
        }.start()
        return countItem
    }
    /////////////////////////////////////////////////////////////////////
    fun insertItem(stockName:String,folderName: String,stockCode: String){
        Thread{
            var index = db.itemDAO().getMaxOrder()
            if (index==null){
                index=0
            }else{
                index
            }

            db.itemDAO().insertItem(ItemTable(itemName = stockName, folderName = folderName, index = index, itemCode = stockCode))
        }.start()
    }
    //해당 폴더에 있는 아이템 조회
    private val _getAllItems = MutableLiveData<List<ItemTable>>()
    val getAllItemsResult:LiveData<List<ItemTable>> get() = _getAllItems
    fun getAllItems(folderName: String){
        Thread{
            _getAllItems.postValue(db.itemDAO().getAll(folderName))
        }.start()
    }


    //폴더 이름 변경
    fun reNameFolder(oldName:String,newName:String):MutableLiveData<String>{
        val liveData : MutableLiveData<String> = MutableLiveData()
        Thread{
            db.folderDAO().updateFolderName(oldName,newName)
            liveData.postValue("finish")
        }.start()
        return liveData
    }
    //동일 종목 체크
    fun itemCheck(folderName: String?,stockName: List<String>):MutableLiveData<Int>{
        val liveData: MutableLiveData<Int> = MutableLiveData()
        Thread{
            if (folderName != null) {
                val int =db.itemDAO().checkItem(folderName,stockName)
                liveData.postValue(int)
            }
        }.start()
        return liveData
    }
    //itemMove
    fun itemMove(folderName: String,stockName:String,index:Int){
        Thread{
            Log.d("dafsdfd","$folderName 테이블, $stockName -> $index")
            db.itemDAO().updateOrder(folderName,stockName,index)
        }.start()
    }

}
