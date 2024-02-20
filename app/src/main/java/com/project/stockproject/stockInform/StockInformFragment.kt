package com.project.stockproject.stockInform

import android.content.Context
import android.content.pm.ActivityInfo
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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.BackKeyHandler
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentStockInformBinding
import com.project.stockproject.search.HistoryManager
import com.project.stockproject.search.Search
import com.project.stockproject.search.SearchAdapter
import com.project.stockproject.search.SearchHistory
import com.project.stockproject.search.SearchHistoryManager
import com.project.stockproject.search.SearchResult
import kotlin.math.abs
import kotlin.math.ceil


class StockInformFragment : Fragment() {
    private lateinit var binding: FragmentStockInformBinding
    private lateinit var viewModel: MyViewModel
    private lateinit var searchManager: SearchHistoryManager
    private lateinit var getStockCode: String
    private lateinit var getStockName: String
    private lateinit var callback: OnBackPressedCallback

    companion object {
        var CURRENT_PAGE: Int? = 0
        var STOCKOUTPUT: StockOutput? = null
    }


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
        searchManager = SearchHistoryManager(MyApplication.getAppContext())

        try{
            //관심종목에서 왔을때
            if (!requireArguments().getString("stockName").isNullOrEmpty()){
                setBackpress("favorite") //뒤로가기 제어
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

                val name = requireArguments().getString("stockName")
                val code = requireArguments().getString("stockCode")
                getStockName = name.toString()
                getStockCode = code.toString()
                binding.searchBar.text = getStockName
            }
            //아니라면 오류발생 ->
        }catch (e:Exception){
            setBackpress("") //뒤로가기 제어
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

            getStockName = searchManager.getSearchHistory().first().stockName
            getStockCode = searchManager.getSearchHistory().first().stockCode
            binding.searchBar.text = getStockName
        }

        //검색창 클릭 결과


        viewModel.setTabLayout(getStockCode).observe(this, Observer {
            STOCKOUTPUT = it
            setTabLayout()
        })


        getStockInform()  //cardView에 정보 입력
        searchView()
        adapterSetting()
    }

    private fun setBackpress(from:String){
        callback = object : OnBackPressedCallback(true) {
            val backKeyHandler = BackKeyHandler(activity)
            override fun handleOnBackPressed() {
                // 뒤로 가기 버튼 처리
                if (from == "favorite"){
                    findNavController().navigate(R.id.action_stockInformFragment_to_favoriteFragment)
                }else{
                    findNavController().navigate(R.id.action_stockInformFragment_to_homeFragment)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.hide()
    }


    //주식 정보 받아와서 viewPager에 뿌리기
    private fun getStockInform() {
        val list = mutableListOf<HistoryManager>()
        val viewPager = binding.stockInforViewPager

        val pagerAdapter = StockInformViewPagerAdapter(mutableListOf(), favoriteClick = {
            val stockName=searchManager.getStockNameByCode(it)
            if (stockName != null) {
                val bundle = bundleOf("stock_name" to stockName,"stock_code" to it)
                findNavController().navigate(R.id.action_stockInformFragment_to_customDialogFavorite,bundle)
            }
        })


        //최근검색기록 10개의 주식 정보 받아오기
        searchManager.getSearchHistory().forEach {
            list.add(it)
        }
        majorIndexViewPager(viewPager, pagerAdapter, list)

        viewModel.singleViewPager(getStockCode, pagerAdapter)
        if (list.size >= 2) {
            Thread.sleep(200)
            viewModel.stockInformRetrofit(list, viewPager, pagerAdapter)
        }

    }

    //주요지수 뷰 페이저 어댑터 세팅
    private fun majorIndexViewPager(
        viewPager2: ViewPager2,
        pagerAdapter: StockInformViewPagerAdapter,
        searchHistorylist: List<HistoryManager>
    ) {
        viewPager2.adapter = pagerAdapter

        viewPager2.apply {
            setPadding(40, 0, 40, 0)
            //viewPager이벤트
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val stockCode = pagerAdapter.getList()[position].stck_shrn_iscd
                    val stockName = searchManager.getStockNameByCode(stockCode)
                    binding.searchBar.text = stockName
                    STOCKOUTPUT = viewModel.getList[position]
                    setTabLayout()

                }
            })

        }
        var transform = CompositePageTransformer()
        transform.addTransformer(MarginPageTransformer(15))
        transform.addTransformer { view: View, fl: Float ->
            var v = 1 - abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        }
        viewPager2.setPageTransformer(transform)
    }

    //////////////////////////////////////////////////////////////////////////////
    private lateinit var tabLayout: TabLayout
    private lateinit var tabAdapter: TabLayoutAdapter
    private lateinit var viewPager2: ViewPager2

    private fun setTabLayout() {
        tabLayout = binding.tabLayout
        viewPager2 = binding.tabViewPager
        tabAdapter = TabLayoutAdapter(activity)
        viewPager2.adapter = tabAdapter
        viewPager2.isUserInputEnabled = false //좌우 스크롤 막기
        viewPager2.currentItem = CURRENT_PAGE!!

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "기본정보"
                1 -> tab.text = "차트"
                2 -> tab.text = "종목토론"
                3 -> tab.text = "뉴스"
                4 -> tab.text = "유튜브"
            }
        }.attach()


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                CURRENT_PAGE = tab?.position?.toInt()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

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
                        val stockName = item.itmsNm
                        val stockCode = item.srtnCd
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
                val bundle = bundleOf("stock_name" to it.stockName,"stock_code" to it.stockCode)
                findNavController().navigate(R.id.action_stockInformFragment_to_customDialogFavorite,bundle)
            }
        )
        binding.searchViewRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }


}


