package com.project.stockproject.stockInform.webView

import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.project.stockproject.stockInform.webView.CustomWebView


class MyChrome(private val webView: CustomWebView, private val activity: FragmentActivity?) : WebChromeClient() {
    var fullscreen: View? = null
    override fun onHideCustomView() {
        fullscreen!!.visibility = View.GONE
        webView.visibility = View.VISIBLE
    }

    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        webView.visibility = View.GONE
        if (fullscreen != null) {
            (activity?.window?.decorView as FrameLayout).removeView(fullscreen)
        }
        fullscreen = view
        (activity?.window?.decorView as FrameLayout).addView(
            fullscreen,
            FrameLayout.LayoutParams(-1, -1)
        )
        fullscreen!!.visibility = View.VISIBLE
    }
}