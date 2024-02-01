package com.project.stockproject.stockInform.tabFragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.databinding.FragmentTabthirdBinding
import com.project.stockproject.stockInform.StockInformFragment.Companion.STOCKOUTPUT
import com.project.stockproject.stockInform.StockOutput




class TabthirdFragment: Fragment() {
    private lateinit var binding: FragmentTabthirdBinding

    private lateinit var viewModel:TabViewModel
    private lateinit var myViewModel:MyViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTabthirdBinding.inflate(layoutInflater,container,false)
        viewModel=ViewModelProviders.of(this)[TabViewModel::class.java]
        myViewModel=ViewModelProviders.of(this)[MyViewModel::class.java]
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        val item= STOCKOUTPUT
        val url = "https://finance.naver.com/item/board.naver?code=${item?.stck_shrn_iscd}"
        webViewSet(url)
    }

    fun webViewSet(url: String) {

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



}