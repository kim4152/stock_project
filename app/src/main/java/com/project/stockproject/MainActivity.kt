package com.project.stockproject

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.project.stockproject.common.MyApplication
import com.project.stockproject.common.SharedViewModel
import com.project.stockproject.databinding.ActivityMainBinding
import com.project.stockproject.retrofit.RetrofitFactory
import com.project.stockproject.retrofit.RetrofitFactory.AWS_EC2_URL
import com.project.stockproject.search.SearchAdapter
import com.project.stockproject.search.SearchHistoryManager


class MainActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var viewModel: MyViewModel
    private lateinit var searchManager : SearchHistoryManager
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.onCreate1(applicationContext)//전역 Context 설정
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProviders.of(this)[MyViewModel::class.java] //viewModel 정의
        getToken() //한국투자증권 토큰 발급
        getIPv4() //IP 주소 얻기

        showAni(isInternetConnected())//처음 앱  켰을 떄 인터넷 확인
        networkChecking() //네트워크 변화 상태 학인
        // NavController 초기화
         navController = findNavController(R.id.mainFrame)

        // Bottom Navigation에 NavController 연결
        val bottomNav = binding.bottomNavigation
        bottomNav.setupWithNavController(navController)


       // setBottomNavigation(bottomNav)//바텀 네비게이션
        searchManager= SearchHistoryManager(MyApplication.getAppContext())

        getInform() //알림
    }

    private fun getInform(){

    }
    private fun getIPv4(){
        viewModel.getIpv4().observe(this, Observer {
            Log.d("dsfadsfs","null")
            AWS_EC2_URL=it
            Log.d("dsfadsfsasdfsdf",it)
            Log.d("dsfadsfsasdfsdf", AWS_EC2_URL)
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    private fun getToken(){
        viewModel.getToken().observe(this, Observer {
            if (it.isNullOrEmpty()){
                MyApplication.makeToast("현재 발급받은 토큰이 없습니다")
            }else{
                RetrofitFactory.TOKEN =it
                sharedViewModel.checkTokenOk("ok")
            }

        })
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


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    //bottom navigation 숨기기
    fun HideBottomNavi(state: Boolean){
        if(state) binding.bottomNavigation.visibility = View.GONE else binding.bottomNavigation.visibility = View.VISIBLE
    }


}