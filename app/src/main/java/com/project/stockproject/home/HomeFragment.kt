package com.project.stockproject.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.common.BackKeyHandler
import com.project.stockproject.common.CustomDecoration
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentHomeBinding
import com.project.stockproject.home.multiFactor.MultiFactorViewPager
import com.project.stockproject.home.tabLayout.RankingAdapter
import com.project.stockproject.search.Search
import com.project.stockproject.search.SearchAdapter
import com.project.stockproject.search.SearchHistory
import com.project.stockproject.search.SearchHistoryManager
import com.project.stockproject.search.SearchResult
import kotlin.math.abs


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MyViewModel
    private lateinit var searchManager: SearchHistoryManager
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var callback: OnBackPressedCallback
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java] //viewModel 정의
        searchManager = SearchHistoryManager(MyApplication.getAppContext())
        setMajorViewPager()//viewPager
        setupOnBoardingIndicators() //indicator init
        adapterSetting()//searchView adapter
        searchView() //searchView setting
        setMultiFactor() //멀티팩터포토폴리오
        setRanking() //탭 레이아웃(예상가격,상승률,거래량)

        setBackpress() //뒤로가기 설정

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private lateinit var rankingAdapter:RankingAdapter
    private fun setRanking(){
        rankingAdapter = RankingAdapter(onClick = {
            val bundle = bundleOf("stockName" to it.stock_name, "stockCode" to it.stock_code,)
            findNavController().navigate(R.id.action_homeFragment_to_stockInformFragment,bundle)
        })
        binding.rankingRecyclerView.apply {
            layoutManager=LinearLayoutManager(requireContext())
            adapter=rankingAdapter
            addItemDecoration(CustomDecoration(1f,10f,ContextCompat.getColor(requireContext(),R.color.outlineVariant)))
        }

        viewModel.topPredic("3000").observe(this, Observer {
            rankingAdapter.submitList(it)
        })
    }

    //멀티팩터포토폴리오
    private lateinit var multiFactorViewPager: MultiFactorViewPager
    private fun setMultiFactor() {
        binding.mfHelp.setOnClickListener { mfHelp() } //?버튼 클릭
        viewModel.getMultiFactor().observe(this, Observer {

            val mfViewPager: ViewPager2 = binding.multiFactorViewPager
            mfViewPager.apply {
                setPadding(40, 0, 40, 0)
                multiFactorViewPager = MultiFactorViewPager(activity!!, it)
                adapter = multiFactorViewPager

                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentOnboardingIndicator2(position)
                    }
                })
            }

            setupOnBoardingIndicators2() //indicator
        })
    }

    //help버튼
    private fun mfHelp(){
        findNavController().navigate(R.id.action_homeFragment_to_MFCustomDialog)
    }


    private fun setMajorViewPager() {
        viewModel.getMajorIndexViewPager(mutableListOf<String>("KOSPI", "KOSDAQ", "KPI200"))
            .observe(viewLifecycleOwner, Observer { it ->

                majorIndexViewPager(it)
            })
    }

    //주요지수 뷰 페이저 어댑터 세팅
    private fun majorIndexViewPager(list: List<MajorIndexViewPagerDTO>) {
        val viewPager2 = binding.majorIndexViewPager
        val adap = MajorIndexViewPagerAdapter(list)
        viewPager2.adapter = adap

        viewPager2.apply {
            setPadding(40, 0, 40, 0)
            //viewPager이벤트
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // 페이지가 선택되었을 때의 동작
                    setCurrentOnboardingIndicator(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // 페이지 스크롤 상태가 변경되었을 때의 동작
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // 페이지가 스크롤될 때의 동작

                }
            })

            offscreenPageLimit = 3
        }
        var transform = CompositePageTransformer()
        transform.addTransformer(MarginPageTransformer(15))
        transform.addTransformer { view: View, fl: Float ->
            var v = 1 - abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        }
        viewPager2.setPageTransformer(transform)
    }

    //주요지수 뷰 인디케이터
    private lateinit var majorIndicators: LinearLayout
    private fun setupOnBoardingIndicators() {
        majorIndicators = binding.majorIndicators
        val indicators =
            arrayOfNulls<ImageView>(3)


        var layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(MyApplication.getAppContext())
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    MyApplication.getAppContext(),
                    R.drawable.onboarding_indicator_inactivie
                )
            )

            indicators[i]?.layoutParams = layoutParams
            majorIndicators?.addView(indicators[i])
        }
    }

    private fun setCurrentOnboardingIndicator(index: Int) {
        var childCount = majorIndicators?.childCount
        for (i in 0 until childCount!!) {
            var imageView = majorIndicators?.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        MyApplication.getAppContext(),
                        R.drawable.onboarding_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        MyApplication.getAppContext(),
                        R.drawable.onboarding_indicator_inactivie
                    )
                )
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    private lateinit var mfIndicators: LinearLayout
    private fun setupOnBoardingIndicators2() {
        mfIndicators = binding.mfIndicators
        val indicators =
            arrayOfNulls<ImageView>(4)


        var layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(MyApplication.getAppContext())
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    MyApplication.getAppContext(),
                    R.drawable.onboarding_indicator_inactivie
                )
            )

            indicators[i]?.layoutParams = layoutParams
            mfIndicators?.addView(indicators[i])
        }
    }

    private fun setCurrentOnboardingIndicator2(index: Int) {
        var childCount = mfIndicators?.childCount
        for (i in 0 until childCount!!) {
            var imageView = mfIndicators?.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        MyApplication.getAppContext(),
                        R.drawable.onboarding_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        MyApplication.getAppContext(),
                        R.drawable.onboarding_indicator_inactivie
                    )
                )
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    //searchView setting, 타자 칠때
    private lateinit var searchFor: String
    private fun searchView() {
        binding.searchView
            .editText.apply {
                //검색버튼 눌렀을때
                /*setOnEditorActionListener { v, actionId, event ->
                    MyApplication.makeToast(v.text.toString())
                    //   binding.searchBar.text = binding.searchView.text
                    binding.searchView.hide()
                    false
                }*/
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
                findNavController().navigate(R.id.action_homeFragment_to_stockInformFragment)

            },
            onStarClick = {
                //즐찾클릭
                val bundle = bundleOf("stock_name" to it.stockName, "stock_code" to it.stockCode)
                findNavController().navigate(
                    R.id.action_homeFragment_to_customDialogFavorite,
                    bundle
                )

            }
        )
        binding.searchViewRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.hide()
    }


    //뒤로가기 설정
    private fun setBackpress() {
        callback = object : OnBackPressedCallback(true) {
            val backKeyHandler = BackKeyHandler(activity)
            override fun handleOnBackPressed() {
                // 뒤로 가기 버튼 처리
                if (binding.searchView.isShowing) {
                    binding.searchView?.hide()
                } else {
                    backKeyHandler.onBackPressed()
                }
            }
        }
    }
}