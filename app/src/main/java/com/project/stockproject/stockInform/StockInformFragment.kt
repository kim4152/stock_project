package com.project.stockproject.stockInform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentStockInformBinding
import com.project.stockproject.room.ItemTable
import com.project.stockproject.search.Search
import com.project.stockproject.search.SearchAdapter
import com.project.stockproject.search.SearchHistory
import com.project.stockproject.search.SearchHistoryManager
import com.project.stockproject.search.SearchResult
import com.project.stockproject.stockInform.chart.MakeChart
import com.project.stockproject.stockInform.openai.ChatRequest
import com.project.stockproject.stockInform.openai.MessageRequest
import java.text.DecimalFormat
import kotlin.math.abs


class StockInformFragment : Fragment() {
    private lateinit var binding: FragmentStockInformBinding
    private lateinit var viewModel: MyViewModel
    private lateinit var tabViewModel: TabViewModel
    private lateinit var searchManager: SearchHistoryManager
    private lateinit var getStockCode: String
    private lateinit var getStockName: String
    private lateinit var callback: OnBackPressedCallback
    private lateinit var pagerAdapter: StockInformViewPagerAdapter
    private lateinit var viewPager2: ViewPager2
    private var stockList: MutableList<StockList> = mutableListOf()
    private var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStockInformBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
        tabViewModel = ViewModelProviders.of(this)[TabViewModel::class.java]
        searchManager = SearchHistoryManager(MyApplication.getAppContext())
        viewPager2 = binding.stockInforViewPager

        binding.stockDiscussion.setOnClickListener { stockDiscussionClick() }

        try {
            //관심종목에서 왔을때
            if (!requireArguments().getString("folderName").isNullOrEmpty()) {
                setBackpress("favorite") //뒤로가기 제어
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
                val name = requireArguments().getString("stockName")
                val code = requireArguments().getString("stockCode")
                val folderName = requireArguments().getString("folderName")
                getStockName = name.toString()
                getStockCode = code.toString()
                binding.searchBar.text = getStockName
                getStockInform(folderName!!)
            }else{
                setBackpress("") //뒤로가기 제어
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

                val name = requireArguments().getString("stockName")
                val code = requireArguments().getString("stockCode")
                Log.d("dfasdf","1111")
                getStockName = name.toString()
                getStockCode = code.toString()
                binding.searchBar.text = getStockName
                getStockInform("search")
            }
            //아니라면 오류발생 ->
        } catch (e: Exception) {
            setBackpress("") //뒤로가기 제어
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
            Log.d("dfasdf","2222")
            getStockName = searchManager.getSearchHistory().first().stockName
            getStockCode = searchManager.getSearchHistory().first().stockCode
            binding.searchBar.text = getStockName
            getStockInform("search")
        }
        searchView()
        adapterSetting()
    }


    //주식 정보 받아와서 viewPager에 뿌리기
    private fun getStockInform(string: String) {
        if (string == "search") {
            //최근검색기록 10개의 주식 정보 받아오기
            searchManager.getSearchHistory().forEach {
                stockList.add(StockList(it.stockCode, it.stockName))
            }
            //검색이 아니라 멀티 팩터에서 왔을때
            if (getStockCode!=stockList[0].stockCode){
                stockList.add(0, StockList(getStockCode,getStockName))
            }
            sendViewModelData()
        } else {
            makeFavoriteList(string)
        }
    }
    private fun makeFavoriteList(folderName: String) {
        viewModel.getAllItems(folderName)
        viewModel.getAllItemsResult.observe(this, Observer {
            val tmpList = it.transform().toMutableList()
            val index = tmpList.indexOfFirst { stockList ->
                stockList.stockName == getStockName
            }
            if (index != -1) {
                // 해당 StockList가 리스트에 존재하는 경우
                val targetStock = tmpList.removeAt(index)
                tmpList.add(0, targetStock)
            }
            stockList=tmpList
            searchManager.getSearchHistory().forEach {historyManager->
                stockList.add(StockList(historyManager.stockCode, historyManager.stockName))
            }

            sendViewModelData()
        })
    }
    private fun List<ItemTable>.transform(): List<StockList> {
        return this.map {
            StockList(
                stockCode = it.itemCode,
                stockName = it.itemName
            )
        }
    }
    private fun sendViewModelData(){
        viewModel.stockInformRetrofit(stockList).observe(this, Observer { i ->
            viewPagerSetting()
            pagerAdapter.addItem(i)
            viewPager2.offscreenPageLimit = i.size + 1
        })
    }


    //주요지수 뷰 페이저 어댑터 세팅
    private fun viewPagerSetting() {
        pagerAdapter = StockInformViewPagerAdapter(mutableListOf(), favoriteClick = {

            viewModel.getStockNameByCode(it).observe(this, Observer {stockName->
                if (stockName != null) {
                    val bundle = bundleOf("stock_name" to stockName, "stock_code" to it)
                    findNavController().navigate(
                        R.id.action_stockInformFragment_to_customDialogFavorite,
                        bundle
                    )
                }
            })
        })

        viewPager2.apply {
            adapter = pagerAdapter
            setPadding(40, 0, 40, 0)
            //viewPager이벤트
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    currentPosition = position
                    val stockCode = pagerAdapter.getList()[currentPosition].stck_shrn_iscd
                    val stockName = stockList[position].stockName
                    binding.searchBar.text = stockName
                    setChart(stockCode, stockName!!)    //차트
                    //setOpenAI(stockCode, stockName) //open ai
                    setStockInfrom()
                }
            })

            //옆 viewPager 보이게
            var transform = CompositePageTransformer()
            transform.addTransformer(MarginPageTransformer(15))
            transform.addTransformer { view: View, fl: Float ->
                var v = 1 - abs(fl)
                view.scaleY = 0.8f + v * 0.2f
            }
            setPageTransformer(transform)
        }

    }


    //////////////////////////////////////////////////////////////////////////////////
//searchView setting, 타자 칠때
    private lateinit var searchFor: String
    private lateinit var searchAdapter: SearchAdapter
    private fun searchView() {
        binding.searchView
            .editText.apply {
                //타자 칠때
                addTextChangedListener {
                    searchFor = it.toString().uppercase()
                    viewModel.cancelSearch()
                    searchUser()
                }
            }


    }

    //타자 친 후 -> viewmodel
    private fun searchUser() {
        if (searchFor.isNotEmpty()) { //입력값이 있을때 검색 시작
            viewModel.search(searchFor).observe(this, Observer {
                if (it.isNotEmpty()) { //리턴값이 있으면 출력
                    var list: MutableList<Search> = mutableListOf()
                    var duplicateCheck: MutableList<String> = mutableListOf()
                    it.forEach { item ->
                        val stockName = item.stock_name
                        val stockCode = item.stock_code
                        if (duplicateCheck.contains(stockCode)) {//중복된 종목 거르기
                        } else {//중복안된 데이터는 추가
                            list.add(SearchResult(null, stockName, stockCode, null))
                            duplicateCheck.add(stockCode)
                        }
                    }
                    searchAdapter.submitList(list)

                } else {

                }
            })
        }//입력값이 있을때 검색 시작 .끝
        else {//입력값이 없으면 최근 기록 출력
            noSearchView()
        }//입력값이 없으면 최근 기록 출력. 끝

    }

    //입력값이 없으면 최근 기록 출력
    private fun noSearchView() {
        var searchHistoryList: MutableList<Search> = mutableListOf()

        searchManager.getSearchHistory().forEach {
            searchHistoryList.add(SearchHistory(null, it.stockName, it.stockCode, null))
        }

        searchAdapter.submitList(searchHistoryList)
    }

    //검색어 어댑터
    private fun adapterSetting() {
        searchAdapter = SearchAdapter(
            onCLick = {
                //종목클릭
                searchManager.addSearchHistory(it.stockName, it.stockCode) //검색 기록 저장
                noSearchView()//검색기록 초기화
                val viewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
                viewModel.setSearchOnclik(it)
                binding.searchView.hide()
                findNavController().navigate(R.id.action_stockInformFragment_self)
            },
            onStarClick = {
                //즐찾클릭
                val bundle = bundleOf("stock_name" to it.stockName, "stock_code" to it.stockCode)
                findNavController().navigate(
                    R.id.action_stockInformFragment_to_customDialogFavorite,
                    bundle
                )
            }
        )
        binding.searchViewRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    ///////////////////////////////////////////////////////////
    private fun setChart(stockCode: String, stockName: String) {
        tabViewModel.cancelPredicCall() //예측값 찾는 Call 중지
        initAnal() //AI 매매 성과 text 초기화
        val marketName = pagerAdapter.getList()[currentPosition].rprs_mrkt_kor_name
        val makeChart = MakeChart(stockCode, binding, tabViewModel, this, marketName)
        makeChart.init()
    }

    private fun initAnal(){
        binding.progressIndicator.visibility=View.VISIBLE
        binding.circle1.text=""
        binding.predicPriceText.text=""
        binding.predicPriceText1.text=""
        binding.predicPriceText2.text=""
    }

    //open ai
    private fun setOpenAI(stockCode: String, stockName: String) {
        val message = "${stockName}의 focus_areas를 json형식으로 한글로 알려줘"

        val chatRequest = ChatRequest(
            messages = listOf(
                MessageRequest(content = message)
            )
        )
        tabViewModel.openAI(chatRequest).observe(this, Observer {

        })
    }

    //투자정보
    private fun setStockInfrom() {
        val stockOuput = pagerAdapter.getList()[currentPosition]
        binding.avls2.text = numberToKorean(stockOuput.hts_avls.toLong())//시가총액
        binding.pbr2.text = "${stockOuput.pbr}배" //pbr
        binding.per2.text = "${stockOuput.per}배"
        binding.eps2.text = "${numberFormat(stockOuput.eps.toDouble().toInt())}원"
        binding.bps2.text = "${numberFormat(stockOuput.bps.toDouble().toInt())}원"

    }

    //시총 변환
    private fun numberToKorean(number: Long): String {
        if (number == 0L) {
            return "0"
        } else if (number < 0) {
            return "${number}억"
        } else {
            val numberList = number.toString()
            if (numberList.length > 4) {
                //조단위
                return "${
                    df.format(
                        numberList.substring(0, numberList.length - 4).toInt()
                    )
                }조 ${df.format(numberList.substring(numberList.length - 4).toInt())}억"
            } else {
                //억단위
                return "${df.format(numberList.toInt())}억"
            }
        }
    }

    val df = DecimalFormat("#,###") //세자리마다 콤마 찍기
    private fun numberFormat(number: Int): String {

        val df = DecimalFormat("#,###") //세자리마다 콤마 찍기
        if (number.toString().substring(0, 1) == "-") {
            return "-${df.format(number.toString().substring(1).toInt())}"
        } else {
            return "${df.format(number.toInt())}"
        }

    }


    private fun stockDiscussionClick() {
        val stockCode = pagerAdapter.getList()[currentPosition].stck_shrn_iscd
        val bundle = bundleOf("stock_code" to stockCode )
        findNavController().navigate(R.id.action_stockInformFragment_to_discusstionDialogFragment2,bundle)
    }




    private fun setBackpress(from: String) {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val bottomNavView = requireActivity().findViewById(R.id.bottom_navigation) as BottomNavigationView
                // 뒤로 가기 버튼 처리
                if (from == "favorite") {
                    findNavController().navigate(R.id.action_stockInformFragment_to_favoriteFragment)
                    bottomNavView.visibility=View.VISIBLE
                } else {
                    findNavController().navigate(R.id.action_stockInformFragment_to_homeFragment)
                    bottomNavView.visibility=View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.hide()
    }

}