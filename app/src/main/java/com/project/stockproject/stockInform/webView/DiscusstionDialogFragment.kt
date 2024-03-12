package com.project.stockproject.stockInform.webView



import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.project.stockproject.MyViewModel
import com.project.stockproject.R
import com.project.stockproject.databinding.FragmentDiscusstionDialogBinding
import com.project.stockproject.stockInform.TabViewModel


class DiscusstionDialogFragment() : DialogFragment() {
    private lateinit var binding: FragmentDiscusstionDialogBinding
    private lateinit var viewModel: TabViewModel
    private lateinit var myViewModel: MyViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // 여백을 투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //애니메이션
        dialog.window?.attributes?.windowAnimations=R.style.SlideUpDialogAnimation
        return dialog
    }

    override fun onResume() {
        super.onResume()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        dialog!!.window!!.setLayout(width, height)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDiscusstionDialogBinding.inflate(layoutInflater,container,false)
        viewModel= ViewModelProviders.of(this)[TabViewModel::class.java]
        myViewModel= ViewModelProviders.of(this)[MyViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stockCode= requireArguments().getString("stock_code")
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




}