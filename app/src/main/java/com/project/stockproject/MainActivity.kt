package com.project.stockproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doBeforeTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stockproject.common.BackKeyHandler
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.ActivityMainBinding
import com.project.stockproject.home.HomeFragment
import com.project.stockproject.search.SearchAdapter
import com.project.stockproject.search.SearchHistory
import com.project.stockproject.search.SearchHistoryManager
import com.project.stockproject.stockInform.StockInformFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var viewModel: MyViewModel
    private lateinit var searchManager : SearchHistoryManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("adfdsf","0:onCreate")
        MyApplication.onCreate1(applicationContext)//전역 Context 설정
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showAni(isInternetConnected())//처음 앱  켰을 떄 인터넷 확인
        networkChecking() //네트워크 변화 상태 학인

        // NavController 초기화
         navController = findNavController(R.id.mainFrame)

        // Bottom Navigation에 NavController 연결
        val bottomNav = binding.bottomNavigation
        bottomNav.setupWithNavController(navController)

       // setBottomNavigation(bottomNav)//바텀 네비게이션
        searchManager= SearchHistoryManager(MyApplication.getAppContext())
        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java] //viewModel 정의


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    //초기 네트워크 상태 확인
    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities =
                connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            // Android M 미만에서는 현재 활성 네트워크가 있는지만 확인
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
    //네트워크 상황에 따른 애니메이션 뷰
    private fun showAni(isConnected:Boolean){
        if (!isConnected){
            binding.lottieLayout.apply {
                isVisible=true
                setOnTouchListener { view, motionEvent -> true }
            }
        }else{
            binding.lottieLayout.isVisible=false
        }
    }
    //네트워크 상태 변화 학인
    private fun networkChecking() {
        val viewModel : MyViewModel = ViewModelProviders.of(this)[MyViewModel::class.java]
        viewModel.getConnectivityLiveData().observe(this, Observer { isConnected ->
            // 네트워크 상태에 따라 처리하는 로직
            showAni(isConnected)
        })
    }
    //bottom navigation
    private fun setBottomNavigation(bottomNavigation:BottomNavigationView){
        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.homeFragment -> {
                    //setTransaction(1)
                    MyApplication.makeToast("1")
                    true
                }
                R.id.stockInformFragment -> {
                    //setTransaction(2)
                    MyApplication.makeToast("2")
                    true
                }
                else -> false
            }
        }
    }
    //fragment 이동 처리
    private fun setTransaction(position:Int){
        val transaction = supportFragmentManager.beginTransaction()

        when(position){
            1->{transaction.replace(R.id.mainFrame, HomeFragment()).addToBackStack(null).commit()}
            2->{transaction.replace(R.id.mainFrame, StockInformFragment()).addToBackStack(null).commit()}
            3->{transaction.replace(R.id.mainFrame, HomeFragment()).addToBackStack(null).commit()}
        }
    }


    //뒤로가기 이벤트
    private val backKeyHandler: BackKeyHandler = BackKeyHandler(this)
    @SuppressLint("MissingSuperCall")
    //override fun onBackPressed() { backKeyHandler.onBackPressed() }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}