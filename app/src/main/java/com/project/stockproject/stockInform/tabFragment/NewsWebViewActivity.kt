package com.project.stockproject.stockInform.tabFragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.project.stockproject.R
import com.project.stockproject.databinding.ActivityNewsWebViewBinding

class NewsWebViewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityNewsWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewsWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url")

        binding.webView.webViewClient= WebViewClient()
        binding.webView.settings.javaScriptEnabled=true

        if (url.isNullOrEmpty()){
            finish()
        }else{
            binding.webView.loadUrl(url)
        }
    }
}