package com.project.stockproject.stockInform.webView

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.project.stockproject.MainActivity
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.databinding.FragmentDiscussionBinding
import com.project.stockproject.stockInform.TabViewModel

class DiscussionFragment : Fragment() {
    private lateinit var binding: FragmentDiscussionBinding
    private lateinit var viewModel: TabViewModel
    private lateinit var myViewModel: MyViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDiscussionBinding.inflate(layoutInflater,container,false)
        viewModel= ViewModelProviders.of(this)[TabViewModel::class.java]
        myViewModel= ViewModelProviders.of(this)[MyViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stockCode= requireArguments().getString("stockCode")
        val url = "https://finance.naver.com/item/board.naver?code=${stockCode}"
        webViewSet(url)
    }

    private fun webViewSet(url: String) {

        val webView=binding.webView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val jsCode = "window.addEventListener('orientationchange', function() { if (window.orientation != 0) { screen.orientation.lock('portrait'); } });"
                webView.evaluateJavascript(jsCode, null)
            }

        }

        webView.webChromeClient = MyChrome(webView, activity)


        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //화면이 계속 켜짐

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

        webView.loadUrl(url)
    }

    override fun onResume() {
        super.onResume()
        //bottom navigation 숨기기
        val mainAct = activity as MainActivity
        mainAct.HideBottomNavi(true)
    }
    override fun onDestroy() {
        super.onDestroy()
        //fragment 꺼질때 바텀네비 보이게
        val mainAct = activity as MainActivity
        mainAct.HideBottomNavi(false)
    }


}