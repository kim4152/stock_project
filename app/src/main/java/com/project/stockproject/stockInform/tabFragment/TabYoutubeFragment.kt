package com.project.stockproject.stockInform.tabFragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProviders
import com.project.stockproject.common.MyApplication
import com.project.stockproject.databinding.FragmentTabfifthBinding
import com.project.stockproject.search.SearchHistoryManager
import com.project.stockproject.stockInform.StockInformFragment.Companion.STOCKOUTPUT
import com.project.stockproject.stockInform.StockOutput


class TabYoutubeFragment() : Fragment() {
    val item: StockOutput? = STOCKOUTPUT
    private lateinit var binding: FragmentTabfifthBinding
    private lateinit var viewModel: TabViewModel
    private lateinit var  searchHistoryManager: SearchHistoryManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = FragmentTabfifthBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProviders.of(this)[TabViewModel::class.java]
        searchHistoryManager= SearchHistoryManager(MyApplication.getAppContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stockName = item?.let { searchHistoryManager.getStockNameByCode(it.stck_shrn_iscd) }
        val url = "https://www.youtube.com/results?search_query=${stockName}"
        item?.let {
            if (stockName != null) {
                webViewSet(binding.webView, url)
            }
        }
    }


    private fun webViewSet(webView: CustomWebView, url: String) {

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true // allow the js


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()

                if (url.startsWith("http://") || url.startsWith("https://")) {
                    // WebView에서 처리할 링크인 경우 기존 동작 유지
                    return false
                } else {
                    // 다른 앱으로 이동하는 링크인 경우 처리
                    openExternalApp(url)

                    return true
                }
            }
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

    private fun openExternalApp(url: String) {
        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        MyApplication.getAppContext().startActivity(intent)
    }
}